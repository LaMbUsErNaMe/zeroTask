package com.example.zero.services

import com.example.zero.controller.dto.customer.response.CustomerInfo
import com.example.zero.controller.dto.order.response.OrderInfo
import com.example.zero.controller.dto.order.response.ResponseOrder
import com.example.zero.enums.OrderStatusType
import io.github.lambusername.exceptionhandler.AccessForbidden
import io.github.lambusername.exceptionhandler.NotFoundException
import com.example.zero.extension.toResponseOrderItem
import com.example.zero.integration.AccountNumberClient
import com.example.zero.integration.InnClient
import com.example.zero.persistence.entity.OrderEntity
import com.example.zero.persistence.entity.OrderItemEntity
import com.example.zero.persistence.repository.CustomerRepository
import com.example.zero.persistence.repository.OrderItemRepository
import com.example.zero.persistence.repository.OrderRepository
import com.example.zero.persistence.repository.ProductRepository
import com.example.zero.projections.OrderInfoProjection
import com.example.zero.services.dto.order.ConfirmationValidationResult
import com.example.zero.services.dto.order.CreateOrderServiceDto
import com.example.zero.services.dto.order.PatchOrderServiceDto
import com.example.zero.services.dto.order.PatchOrderStatusServiceDto
import com.example.zero.services.dto.order.OrderConfirmationContext
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.async
import kotlinx.coroutines.future.await
import kotlinx.coroutines.runBlocking
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.time.LocalDateTime
import java.time.LocalDate
import java.util.UUID

@Service
class OrderServiceImpl(
    private val customerRepository: CustomerRepository,
    private val orderRepository: OrderRepository,
    private val productRepository: ProductRepository,
    private val orderItemRepository: OrderItemRepository,
    private val accountNumberClient: AccountNumberClient,
    private val innClient: InnClient,
    private val integrationDispatcher: CoroutineDispatcher
) : OrderService{

    @Transactional
    override fun save(customerId: Long, request: CreateOrderServiceDto) : UUID{

        if (!customerRepository.existsById(customerId)) {
            throw NotFoundException("Клиент не найден!")
        }

        val productIds = request.products.map { it.productId }.distinct()

        val products = productRepository.findAllById(productIds)
            .associateBy { it.id!! }

        val missingProducts = productIds.filterNot { products.containsKey(it) }
        if (missingProducts.isNotEmpty()) {
            throw NotFoundException("Заказ не создан!Товары не найдены: $missingProducts")
        }

        val notAvailableProds = products.values.filterNot { it.isAvailable}
        if (notAvailableProds.isNotEmpty()) {
            val notAvailableIds = notAvailableProds.map { it.id!! }
            throw NotFoundException("Заказ не создан! Товары недоступны: $notAvailableIds")
        }

        val quantityNotEnoughProducts = request.products.filter { productFromDto ->
            products[productFromDto.productId]!!.quantity < productFromDto.quantity
        }

        if (!quantityNotEnoughProducts.isEmpty())
            throw NotFoundException("Не все товары в достаточном кол-ве! " +
                    "Товаров не хватает: $quantityNotEnoughProducts")

        val order = OrderEntity(
            customer = customerRepository.getReferenceById(customerId),
            deliveryAddress = request.deliveryAddress
        )
        val savedOrder = orderRepository.save(order)

        request.products.forEach { productFromDto ->
            val product = products[productFromDto.productId]!!
            product.quantity -= productFromDto.quantity
            product.quantityChangedDateTime = LocalDateTime.now()
        }

        productRepository.saveAll(products.values)

        val orderItems = request.products.map { productFromDto ->
            val product = products[productFromDto.productId]!!

            OrderItemEntity(
                order = savedOrder,
                product = product,
                productPrice = product.price,
                quantity = productFromDto.quantity
            )
        }

        orderItemRepository.saveAll(orderItems)

        return savedOrder.id!!
    }

    @Transactional
    override fun patch(
        customerId: Long,
        id: UUID,
        request: PatchOrderServiceDto
    ) {
        val order = existsChekAndGetOrder(customerId, id)

        if (order.status != OrderStatusType.CREATED){
            throw AccessForbidden("Этот заказ нельзя изменять!")
        }

        order.deliveryAddress = request.deliveryAddress

        orderRepository.save(order)

        val oldOrderItems = orderItemRepository.findOrderProductsForUpdate(id)

        val requestedProducts = request.products
            .groupBy { it.productId }
            .mapValues { (_, items) ->
                items.fold(BigDecimal.ZERO) { quantity, item -> quantity + item.quantity }
            }

        val productIds = requestedProducts.keys

        val products = productRepository.findAllById(productIds)
            .associateBy { it.id!! }

        val missingProducts = productIds.filterNot { products.containsKey(it) }
        if (missingProducts.isNotEmpty()) {
            throw NotFoundException("Заказ не обновлён!Товары не найдены: $missingProducts")
        }

        val notAvailableProds = products.values.filter { !it.isAvailable}
        if (notAvailableProds.isNotEmpty()) {
            val notAvailableIds = notAvailableProds.map { it.id!! }
            throw NotFoundException("Заказ не создан! Товары недоступны: $notAvailableIds")
        }

        val quantityNotEnoughProducts = requestedProducts.filter { (productId, requestedQuantity) ->
            val oldQuantity = oldOrderItems
                .firstOrNull { it.productId == productId }
                ?.quantity
                ?: BigDecimal.ZERO
            val delta = requestedQuantity - oldQuantity

            delta > BigDecimal.ZERO && products[productId]!!.quantity < delta
        }
        if (quantityNotEnoughProducts.isNotEmpty())
            throw NotFoundException("Заказ не обновлён! Не все товары в достаточном кол-ве!")

        requestedProducts.forEach { (productId, requestedQuantity) ->
            val oldQuantity = oldOrderItems
                .firstOrNull { it.productId == productId }
                ?.quantity
                ?: BigDecimal.ZERO
            val delta = requestedQuantity - oldQuantity

            if (delta != BigDecimal.ZERO) {
                val product = products[productId]!!
                product.quantity -= delta
                product.quantityChangedDateTime = LocalDateTime.now()
            }
        }

        productRepository.saveAll(products.values)

        val itemsToSave = mutableListOf<OrderItemEntity>()

        for ((productId, requestedQuantity) in requestedProducts) {
            val product = products[productId]!!

            val existing = oldOrderItems.find {
                it.productId == productId
            }

            if (existing != null) {
                val existingItem = OrderItemEntity(
                    id = existing.id,
                    order = order,
                    product = product,
                    productPrice = existing.productPrice,
                    quantity = requestedQuantity
                )
                itemsToSave.add(existingItem)
            } else {
                val newItem = OrderItemEntity(
                    order = order,
                    product = product,
                    productPrice = product.price,
                    quantity = requestedQuantity
                )
                itemsToSave.add(newItem)
            }
        }
        orderItemRepository.saveAll(itemsToSave)
    }

    override fun findById(customerId: Long, id: UUID): ResponseOrder {
        val order = existsChekAndGetOrder(customerId, id)

        val itemsFromProj = orderItemRepository.findOrderProducts(id)

        val converted = itemsFromProj.map { it.toResponseOrderItem() }

        val totalPrice = converted.sumOf { it.productPrice.multiply(it.quantity) }

        val response = ResponseOrder(
            orderId = id,
            products = converted,
            totalPrice = totalPrice,
            status = order.status,
            deliveryDate = order.deliveryDate,
        )
        return response
    }

    @Transactional
    override fun softDeleteById(customerId: Long, id: UUID) {
        val order = existsChekAndGetOrder(customerId, id)

        if (order.status == OrderStatusType.CANCELED) throw NotFoundException("Этот заказ уже удалён!")

        val orderItems = orderItemRepository.findOrderProducts(id)

        val productIds = orderItems.map { it.productId }

        val products = productRepository.findAllById(productIds)
            .associateBy { it.id!! }

        orderItems.forEach { productFromOrder ->
            val product = products[productFromOrder.productId]!!
            product.quantity += productFromOrder.quantity
            product.quantityChangedDateTime = LocalDateTime.now()
        }

        productRepository.saveAll(products.values)

        patchStatus(id,PatchOrderStatusServiceDto(OrderStatusType.CANCELED))
    }

    @Transactional
    override fun patchStatus(id: UUID, dto: PatchOrderStatusServiceDto) {
        val updated = orderRepository.updateStatus(id, dto.status)
        if (updated == 0) throw NotFoundException("Заказ не найден!")
    }

    @Transactional
    override fun getOrdersInfoByProduct(): Map<UUID, List<OrderInfo>> = runBlocking {

        val ordersByProduct = existsChekAndGetOrdersByProduct()

        val logins = ordersByProduct.map { it.customerLogin }.distinct()

        val accountNumbersDeferred = async(integrationDispatcher) {
            logger.info{ "Thread: ${Thread.currentThread().name} getAccountNumbersSuspended" }
            accountNumberClient.getAccountNumbersSuspended(logins)
        }

        val innsDeferred = async(integrationDispatcher) {
            logger.info{ "Thread: ${Thread.currentThread().name} getInnsFuture" }
            innClient.getInnsFuture(logins).await()
        }

        val accountNumbers = accountNumbersDeferred.await()
        val inns = innsDeferred.await()

        ordersByProduct
            .groupBy(
                { it.productId },
                { order ->
                    OrderInfo(
                        id = order.orderId,
                        customer = CustomerInfo(
                            id = order.customerId,
                            email = order.customerLogin,
                            accountNumber = accountNumbers[order.customerLogin] ?: "ОТСУТСТВУЕТ!",
                            inn = inns[order.customerLogin] ?: "ОТСУТСТВУЕТ!"
                        ),
                        status = order.status,
                        deliveryAddress = order.deliveryAddress,
                        quantity = order.quantity
                    )
                }
            )

    }

    @Transactional
    override fun confirm(customerId: Long, id: UUID) {
        val order = existsChekAndGetOrder(customerId, id)
        if (order.status == OrderStatusType.CONFIRMED) return
        check(order.status == OrderStatusType.PROCESSING) {
            "Заказ [$id] нельзя подтвердить из статуса ${order.status}"
        }

        val updated = orderRepository.updateStatusIfCurrent(
            id = id,
            expectedStatus = order.status,
            newStatus = OrderStatusType.CONFIRMED,
        )
        check(updated == 1) { "Статус заказа [$id] был изменён параллельно" }
    }

    @Transactional(readOnly = true)
    override fun checkOrderOwner(customerId: Long, id: UUID) {
        existsChekAndGetOrder(customerId, id)
    }

    @Transactional(readOnly = true)
    override fun canStartConfirmation(customerId: Long, id: UUID): Boolean {
        val order = existsChekAndGetOrder(customerId, id)
        return order.status == OrderStatusType.CREATED ||
            (order.status == OrderStatusType.PROCESSING && order.processBusinessKey != null)
    }

    @Transactional(readOnly = true)
    override fun getConfirmationContext(customerId: Long, id: UUID): OrderConfirmationContext {
        val order = existsChekAndGetOrder(customerId, id)
        check(order.status == OrderStatusType.CREATED || order.status == OrderStatusType.PROCESSING) {
            "Order [$id] cannot be confirmed from status ${order.status}"
        }

        val login = order.customer.login
        val items = orderItemRepository.findOrderProducts(id)
        check(items.isNotEmpty()) { "Order [$id] has no items" }
        val inn = innClient.getInnsBlocking(listOf(login))[login]
            ?: error("INN was not returned for customer $login")
        val accountNumber = accountNumberClient.getAccountNumbersBlocking(listOf(login))[login]
            ?: error("Account number was not returned for customer $login")

        return OrderConfirmationContext(
            orderId = id,
            customerId = customerId,
            login = login,
            deliveryAddress = order.deliveryAddress,
            inn = inn,
            accountNumber = accountNumber,
            amount = items.sumOf { it.productPrice.multiply(it.quantity) },
            existingBusinessKey = order.processBusinessKey,
        )
    }

    @Transactional
    override fun startProcessing(customerId: Long, id: UUID, businessKey: String) {
        val order = existsChekAndGetOrder(customerId, id)
        if (order.status == OrderStatusType.PROCESSING && order.processBusinessKey == businessKey) return

        val updated = orderRepository.startProcessing(
            id = id,
            expectedStatus = OrderStatusType.CREATED,
            newStatus = OrderStatusType.PROCESSING,
            businessKey = businessKey,
        )
        check(updated == 1) { "Order [$id] is already processing or was changed concurrently" }
    }

    @Transactional
    override fun completeConfirmation(id: UUID, deliveryDate: LocalDate) {
        val updated = orderRepository.completeConfirmation(
            id = id,
            status = OrderStatusType.CONFIRMED,
            deliveryDate = deliveryDate,
        )
        check(updated == 1) { "Order [$id] was not found" }
    }

    @Transactional(readOnly = true)
    override fun confirmationValidation(customerId: Long, id: UUID): ConfirmationValidationResult {
        val order = existsChekAndGetOrder(customerId, id)
        val reason = when {
            order.status != OrderStatusType.PROCESSING ->
                "Заказ находится в неподходящем статусе: ${order.status}"

            !order.customer.isActive ->
                "Пользователь неактивен"

            !orderItemRepository.existsByOrderId(id) ->
                "В заказе отсутствуют товары"

            else -> null
        }

        return ConfirmationValidationResult(
            valid = reason == null,
            reason = reason,
        )
    }

    @Transactional
    override fun releaseReservation(id: UUID) {
        val orderItems = orderItemRepository.findOrderProducts(id)
        val products = productRepository.findAllById(orderItems.map { it.productId })
            .associateBy { it.id!! }

        orderItems.forEach { item ->
            val product = products[item.productId]
                ?: throw NotFoundException("Товар [${item.productId}] из заказа [$id] не найден")
            product.quantity += item.quantity
            product.quantityChangedDateTime = LocalDateTime.now()
        }
        productRepository.saveAll(products.values)
    }

    @Transactional
    override fun rejectConfirmation(id: UUID, reason: String?) {
        val order = orderRepository.findByIdOrNull(id)
            ?: throw NotFoundException("Заказ [$id] не найден!")
        if (order.status == OrderStatusType.REJECTED) return
        check(order.status == OrderStatusType.PROCESSING) {
            "Заказ [$id] нельзя отклонить из статуса ${order.status}"
        }

        val updated = orderRepository.updateStatusIfCurrent(
            id = id,
            expectedStatus = order.status,
            newStatus = OrderStatusType.REJECTED,
        )
        check(updated == 1) { "Статус заказа [$id] был изменён параллельно" }
        logger.info { "Заказ [$id] отклонён: ${reason ?: "причина не указана"}" }
    }

    @Transactional
    override fun cancelConfirmation(id: UUID, reason: String?) {
        val order = orderRepository.findByIdOrNull(id)
            ?: throw NotFoundException("Заказ [$id] не найден!")
        if (order.status == OrderStatusType.CANCELED) return
        check(order.status == OrderStatusType.PROCESSING) {
            "Заказ [$id] нельзя отменить из статуса ${order.status}"
        }

        val updated = orderRepository.updateStatusIfCurrent(
            id = id,
            expectedStatus = order.status,
            newStatus = OrderStatusType.CANCELED,
        )
        check(updated == 1) { "Статус заказа [$id] был изменён параллельно" }
        logger.info { "Подтверждение заказа [$id] отменено: ${reason ?: "причина не указана"}" }
    }

    private fun existsChekAndGetOrder(customerId: Long, id: UUID): OrderEntity {
        val order = orderRepository.findByIdOrNull(id)
            ?: throw NotFoundException("Заказ [$id] не найден!")
        if(order.customer.id != customerId){
            throw AccessForbidden("Доступа нет!")
        }
        return order
    }

    private fun existsChekAndGetOrdersByProduct(): List<OrderInfoProjection> {
        val ordersByProduct = orderRepository.getOrderInfoProjectionsByStatusIn(ALLOWED_STATUSES)
        if (ordersByProduct.isEmpty()) {
            throw NotFoundException("Нет актуальных заказов!")
        }
        return ordersByProduct
    }

    private companion object {
        val logger = KotlinLogging.logger {}
        val ALLOWED_STATUSES = listOf(OrderStatusType.CONFIRMED, OrderStatusType.CREATED)
    }
}
