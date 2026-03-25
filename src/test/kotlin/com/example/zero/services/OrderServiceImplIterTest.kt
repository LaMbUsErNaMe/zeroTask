package com.example.zero.services

import com.example.zero.enums.CategoryType
import com.example.zero.enums.OrderStatusType
import com.example.zero.integration.AccountNumberClient
import com.example.zero.integration.InnClient
import com.example.zero.persistence.entity.CustomerEntity
import com.example.zero.persistence.entity.OrderEntity
import com.example.zero.persistence.entity.OrderItemEntity
import com.example.zero.persistence.entity.ProductEntity
import com.example.zero.persistence.repository.CustomerRepository
import com.example.zero.persistence.repository.OrderItemRepository
import com.example.zero.persistence.repository.OrderRepository
import com.example.zero.persistence.repository.ProductRepository
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest
import org.springframework.test.context.ActiveProfiles
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.concurrent.CompletableFuture

@DataJpaTest
@ActiveProfiles("test")
class OrderServiceImplJpaTest {

    @Autowired
    lateinit var customerRepository: CustomerRepository

    @Autowired
    lateinit var productRepository: ProductRepository

    @Autowired
    lateinit var orderRepository: OrderRepository

    @Autowired
    lateinit var orderItemRepository: OrderItemRepository

    private lateinit var accountNumberClient: AccountNumberClient
    private lateinit var innClient: InnClient
    private lateinit var orderService: OrderServiceImpl

    @BeforeEach
    fun prepare() {
        accountNumberClient = mockk()
        innClient = mockk()

        orderService = OrderServiceImpl(
            customerRepository = customerRepository,
            orderRepository = orderRepository,
            productRepository = productRepository,
            orderItemRepository = orderItemRepository,
            accountNumberClient = accountNumberClient,
            innClient = innClient,
            integrationDispatcher = Dispatchers.Unconfined,
        )
    }

    @Test
    fun `getOrdersInfoByProduct returns all active products grouped by product id`() {
        val customer1 = customerRepository.save(
            CustomerEntity(
                login = "login1",
                email = "email1",
                isActive = true,
            )
        )
        val customer2 = customerRepository.save(
            CustomerEntity(
                login = "login2",
                email = "email2",
                isActive = true,
            )
        )
        val customer3 = customerRepository.save(
            CustomerEntity(
                login = "login3",
                email = "email3",
                isActive = true,
            )
        )

        val category = CategoryType.entries.first()

        val product1 = productRepository.save(
            ProductEntity(
                name = "prod1",
                productNumber = 1001L,
                categoryType = category,
                price = BigDecimal("10"),
                quantity = BigDecimal("100"),
                quantityChangedDateTime = LocalDateTime.now(),
                createdDate = LocalDate.now(),
                isAvailable = true,
            )
        )
        val product2 = productRepository.save(
            ProductEntity(
                name = "prod2",
                productNumber = 1002L,
                categoryType = category,
                price = BigDecimal("20"),
                quantity = BigDecimal("100"),
                quantityChangedDateTime = LocalDateTime.now(),
                createdDate = LocalDate.now(),
                isAvailable = true,
            )
        )
        val product3 = productRepository.save(
            ProductEntity(
                name = "prod3",
                productNumber = 1003L,
                categoryType = category,
                price = BigDecimal("30"),
                quantity = BigDecimal("100"),
                quantityChangedDateTime = LocalDateTime.now(),
                createdDate = LocalDate.now(),
                isAvailable = true,
            )
        )
        val product4 = productRepository.save(
            ProductEntity(
                name = "prod4",
                productNumber = 1004L,
                categoryType = category,
                price = BigDecimal("40"),
                quantity = BigDecimal("100"),
                quantityChangedDateTime = LocalDateTime.now(),
                createdDate = LocalDate.now(),
                isAvailable = true,
            )
        )

        val order1 = orderRepository.save(
            OrderEntity(
                customer = customer1,
                status = OrderStatusType.CREATED,
                deliveryAddress = "addr1",
            )
        )
        val order2 = orderRepository.save(
            OrderEntity(
                customer = customer2,
                status = OrderStatusType.CONFIRMED,
                deliveryAddress = "addr2",
            )
        )
        val order3 = orderRepository.save(
            OrderEntity(
                customer = customer3,
                status = OrderStatusType.CANCELED,
                deliveryAddress = "addr3",
            )
        )

        orderItemRepository.save(
            OrderItemEntity(
                order = order1,
                product = product1,
                productPrice = product1.price,
                quantity = BigDecimal("1"),
            )
        )
        orderItemRepository.save(
            OrderItemEntity(
                order = order2,
                product = product1,
                productPrice = product1.price,
                quantity = BigDecimal("2"),
            )
        )
        orderItemRepository.save(
            OrderItemEntity(
                order = order2,
                product = product2,
                productPrice = product2.price,
                quantity = BigDecimal("3"),
            )
        )
        orderItemRepository.save(
            OrderItemEntity(
                order = order3,
                product = product2,
                productPrice = product2.price,
                quantity = BigDecimal("4"),
            )
        )
        orderItemRepository.save(
            OrderItemEntity(
                order = order3,
                product = product3,
                productPrice = product3.price,
                quantity = BigDecimal("5"),
            )
        )

        coEvery { accountNumberClient.getAccountNumbersSuspended(any()) } returns mapOf(
            "login1" to "ACC1",
            "login2" to "ACC2",
            "login3" to "ACC3",
        )

        every { innClient.getInnsFuture(any()) } returns CompletableFuture.completedFuture(
            mapOf(
                "login1" to "INN1",
                "login2" to "INN2",
                "login3" to "INN3",
            )
        )

        val result = orderService.getOrdersInfoByProduct()

        assertThat(result.keys)
            .containsExactlyInAnyOrder(product1.id!!, product2.id!!)

        assertThat(result)
            .doesNotContainKeys(product3.id!!, product4.id!!)

        val product1Orders = result[product1.id!!]
        assertThat(product1Orders).isNotNull
        assertThat(product1Orders!!).hasSize(2)

        assertThat(product1Orders.map { it.id })
            .containsExactlyInAnyOrder(order1.id!!, order2.id!!)

        val product2Orders = result[product2.id!!]
        assertThat(product2Orders).isNotNull
        assertThat(product2Orders!!).hasSize(1)
        assertThat(product2Orders.single().id).isEqualTo(order2.id!!)
    }
}