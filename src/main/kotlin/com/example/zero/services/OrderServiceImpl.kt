package com.example.zero.services

import com.example.zero.controller.dto.order.request.patch.PatchOrderRequest
import com.example.zero.controller.dto.order.request.patch.PatchOrderStatusRequest
import com.example.zero.controller.dto.order.response.ResponseOrder
import com.example.zero.enums.OrderStatusType
import com.example.zero.exception.AccessForbidden
import com.example.zero.exception.NotFoundException
import com.example.zero.persistence.entity.OrderEntity
import com.example.zero.persistence.entity.OrderItemEntity
import com.example.zero.persistence.repository.CustomerRepository
import com.example.zero.persistence.repository.OrderItemRepository
import com.example.zero.persistence.repository.OrderRepository
import com.example.zero.persistence.repository.ProductRepository
import com.example.zero.services.dto.order.CreateOrderServiceDto
import com.example.zero.services.dto.order.PatchOrderServiceDto
import com.example.zero.services.dto.order.PatchOrderStatusServiceDto
import com.example.zero.services.dto.product.PatchProductServiceDto
import org.springframework.data.repository.findByIdOrNull
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.util.UUID

@Service
class OrderServiceImpl(
    private val customerRepository: CustomerRepository,
    private val orderRepository: OrderRepository,
    private val productRepository: ProductRepository,
    private val orderItemRepository: OrderItemRepository
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

        val quantityNotEnoughProducts = request.products.filter { productFromDto ->
            products[productFromDto.productId]!!.quantity < productFromDto.quantity
        }
        if (!quantityNotEnoughProducts.isEmpty())
            throw NotFoundException("Не все товары в достаточном кол-ве!")

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
                product = productRepository.getReferenceById(productFromDto.productId),
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

        val oldOrderItems = orderItemRepository.findOrderProductsForUpdate(id)

        val productIds = request.products.map { it.productId }.distinct()

        val products = productRepository.findAllById(productIds)
            .associateBy { it.id!! }

        val missingProducts = productIds.filterNot { products.containsKey(it) }
        if (missingProducts.isNotEmpty()) {
            throw NotFoundException("Заказ не обновлён!Товары не найдены: $missingProducts")
        }

        val quantityNotEnoughProducts = request.products.filter { productFromDto ->
            products[productFromDto.productId]!!.quantity < productFromDto.quantity
        }
        if (!quantityNotEnoughProducts.isEmpty())
            throw NotFoundException("Заказ не обновлён! Не все товары в достаточном кол-ве!")


        request.products.forEach { productFromDto ->
            val product = products[productFromDto.productId]!!
            product.quantity -= productFromDto.quantity
            product.quantityChangedDateTime = LocalDateTime.now()
        }

        productRepository.saveAll(products.values)

        val itemsToSave = mutableListOf<OrderItemEntity>()

        for (productFromDto in request.products) {
            val product = products[productFromDto.productId]!!

            val existing = oldOrderItems.find {
                it.productId == productFromDto.productId &&
                        it.productPrice == product.price
            }

            if (existing != null) {
                val existingItem = OrderItemEntity(
                    id = existing.id,
                    order = order,
                    product = product,
                    productPrice = existing.productPrice,
                    quantity = existing.quantity + productFromDto.quantity
                )
                itemsToSave.add(existingItem)
            } else {
                val newItem = OrderItemEntity(
                    order = order,
                    product = productRepository.getReferenceById(productFromDto.productId),
                    productPrice = product.price,
                    quantity = productFromDto.quantity
                )
                itemsToSave.add(newItem)
            }
        }
        orderItemRepository.saveAll(itemsToSave)
    }

    override fun findById(customerId: Long, id: UUID): ResponseOrder {
        existsChekAndGetOrder(customerId, id)

        val items = orderItemRepository.findOrderProducts(id)

        val totalPrice = items.sumOf { it.productPrice.multiply(it.quantity) }

        val response = ResponseOrder(
            orderId = id,
            products = items,
            totalPrice = totalPrice
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
            product.quantity -= productFromOrder.quantity
            product.quantityChangedDateTime = LocalDateTime.now()
        }

        productRepository.saveAll(products.values)

        patchStatus(id,PatchOrderStatusServiceDto(OrderStatusType.CANCELED))
    }

    override fun patchStatus(id: UUID, dto: PatchOrderStatusServiceDto) {

        val updated = orderRepository.updateStatus(id, dto.status)
        if (updated == 0) throw NotFoundException("Заказ не найден!")
    }

    override fun existsChekAndGetOrder(customerId: Long, id: UUID): OrderEntity {
        val order = orderRepository.findByIdOrNull(id)
            ?: throw NotFoundException("Заказ [$id] не найден!")
        if(order.customer.id != customerId){
            throw AccessForbidden("Доступа нет!")
        }
        return order
    }

    override fun confirm(customerId: Long, id: UUID) {
        existsChekAndGetOrder(customerId, id)
        patchStatus(id,PatchOrderStatusServiceDto(OrderStatusType.CONFIRMED))
    }
}