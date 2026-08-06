package com.example.zero.orchestrator

import com.example.zero.enums.CategoryType
import com.example.zero.enums.OrderStatusType
import com.example.zero.orchestartor.OrderConfirmationProcessService
import com.example.zero.persistence.entity.CustomerEntity
import com.example.zero.persistence.entity.OrderEntity
import com.example.zero.persistence.entity.OrderItemEntity
import com.example.zero.persistence.entity.ProductEntity
import com.example.zero.persistence.repository.CustomerRepository
import com.example.zero.persistence.repository.OrderItemRepository
import com.example.zero.persistence.repository.OrderRepository
import com.example.zero.persistence.repository.ProductRepository
import com.github.tomakehurst.wiremock.client.WireMock.*
import com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig
import com.github.tomakehurst.wiremock.WireMockServer
import org.assertj.core.api.Assertions.assertThat
import org.awaitility.Awaitility.await
import org.camunda.bpm.engine.RuntimeService
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.kafka.test.context.EmbeddedKafka
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import java.math.BigDecimal
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime

@SpringBootTest(
    properties = [
        "app.kafka.enabled=true",
        "app.compliance.stub-enabled=true",
        "spring.kafka.bootstrap-servers=\${spring.embedded.kafka.brokers}",
        "spring.kafka.consumer.group-id=zero-system-test",
        "spring.kafka.consumer.auto-offset-reset=earliest",
        "spring.kafka.listener.ack-mode=record",
        "spring.kafka.listener.concurrency=1",
        "spring.kafka.producer.key-serializer=org.apache.kafka.common.serialization.StringSerializer",
        "spring.kafka.producer.value-serializer=org.apache.kafka.common.serialization.StringSerializer",
        "camunda.bpm.job-execution.enabled=true",
        "spring.datasource.url=jdbc:h2:mem:orchestrator-test;DB_CLOSE_DELAY=-1",
        "spring.sql.init.mode=never",
        "spring.jpa.hibernate.ddl-auto=create-drop",
    ]
)
@ActiveProfiles("test")
@EmbeddedKafka(partitions = 1, topics = ["test_topic", "compliance-request", "compliance-result"])
class OrderConfirmationSystemTest {

    @Autowired lateinit var customerRepository: CustomerRepository
    @Autowired lateinit var productRepository: ProductRepository
    @Autowired lateinit var orderRepository: OrderRepository
    @Autowired lateinit var orderItemRepository: OrderItemRepository
    @Autowired lateinit var processService: OrderConfirmationProcessService
    @Autowired lateinit var runtimeService: RuntimeService

    @BeforeEach
    fun resetWireMock() {
        configureFor("localhost", wireMock.port())
        wireMock.resetAll()
    }

    @Test
    fun `whole process confirms order and persists delivery date`() {
        val customer = customerRepository.save(
            CustomerEntity(login = "system-user", email = "system-user@example.test", isActive = true)
        )
        val product = productRepository.save(
            ProductEntity(
                name = "system-product",
                productNumber = 900001,
                categoryType = CategoryType.entries.first(),
                price = BigDecimal("125.50"),
                quantity = BigDecimal("8"),
                quantityChangedDateTime = LocalDateTime.now(),
                createdDate = LocalDate.now(),
                isAvailable = true,
            )
        )
        val order = orderRepository.save(
            OrderEntity(customer = customer, deliveryAddress = "Test street, 1")
        )
        orderItemRepository.save(
            OrderItemEntity(
                order = order,
                product = product,
                productPrice = product.price,
                quantity = BigDecimal("2"),
            )
        )

        stubFor(post(urlEqualTo("/accounts")).willReturn(okJson("{\"system-user\":\"ACC-001\"}")))
        stubFor(post(urlEqualTo("/inns")).willReturn(okJson("{\"system-user\":\"7700000000\"}")))
        stubFor(post(urlEqualTo("/contracts")).willReturn(okJson("{\"contractId\":\"CONTRACT-1\"}")))
        stubFor(post(urlEqualTo("/deliveries")).willReturn(okJson("{\"deliveryDate\":\"2026-08-10\"}")))
        stubFor(post(urlEqualTo("/payments")).willReturn(okJson("{\"success\":true}")))
        stubFor(post(urlEqualTo("/notifications")).willReturn(aResponse().withStatus(204)))

        val businessKey = processService.start(customer.id!!, order.id!!)

        assertThat(businessKey).isNotBlank()
        await().atMost(Duration.ofSeconds(40)).untilAsserted {
            val confirmed = orderRepository.findById(order.id!!).orElseThrow()
            assertThat(confirmed.status).isEqualTo(OrderStatusType.CONFIRMED)
            assertThat(confirmed.deliveryDate).isEqualTo(LocalDate.of(2026, 8, 10))
            assertThat(confirmed.processBusinessKey).isEqualTo(businessKey)
        }

        verify(postRequestedFor(urlEqualTo("/contracts"))
            .withRequestBody(matchingJsonPath("$.inn", equalTo("7700000000")))
            .withRequestBody(matchingJsonPath("$.accountNumber", equalTo("ACC-001"))))
        verify(postRequestedFor(urlEqualTo("/deliveries"))
            .withRequestBody(matchingJsonPath("$.deliveryAddress", equalTo("Test street, 1"))))
        verify(postRequestedFor(urlEqualTo("/payments"))
            .withRequestBody(matchingJsonPath("$.amount", equalTo("251.0"))))
    }

    @Test
    fun `failed compliance rejects order after contract and skips payment and delivery`() {
        val customer = customerRepository.save(
            CustomerEntity(login = "fraud-user", email = "fraud-user@example.test", isActive = true)
        )
        val product = productRepository.save(
            ProductEntity(
                name = "rejected-product",
                productNumber = 900002,
                categoryType = CategoryType.entries.first(),
                price = BigDecimal("50.00"),
                quantity = BigDecimal("4"),
                quantityChangedDateTime = LocalDateTime.now(),
                createdDate = LocalDate.now(),
                isAvailable = true,
            )
        )
        val order = orderRepository.save(OrderEntity(customer = customer, deliveryAddress = "Risk street, 13"))
        orderItemRepository.save(
            OrderItemEntity(order = order, product = product, productPrice = product.price, quantity = BigDecimal.ONE)
        )

        stubFor(post(urlEqualTo("/accounts")).willReturn(okJson("{\"fraud-user\":\"ACC-RISK\"}")))
        stubFor(post(urlEqualTo("/inns")).willReturn(okJson("{\"fraud-user\":\"6660000000\"}")))
        stubFor(post(urlEqualTo("/contracts")).willReturn(okJson("{\"contractId\":\"CONTRACT-RISK\"}")))
        stubFor(post(urlEqualTo("/notifications")).willReturn(aResponse().withStatus(204)))

        processService.start(customer.id!!, order.id!!)

        await().atMost(Duration.ofSeconds(40)).untilAsserted {
            assertThat(orderRepository.findById(order.id!!).orElseThrow().status)
                .isEqualTo(OrderStatusType.REJECTED)
            assertThat(wireMock.findAll(postRequestedFor(urlEqualTo("/notifications"))))
                .isNotEmpty()
        }

        verify(postRequestedFor(urlEqualTo("/contracts")))
        verify(0, postRequestedFor(urlEqualTo("/deliveries")))
        verify(0, postRequestedFor(urlEqualTo("/payments")))
        verify(postRequestedFor(urlEqualTo("/notifications"))
            .withRequestBody(matchingJsonPath("$.message", containing("Fraud risk detected"))))
    }

    @Test
    fun `payment failure compensates completed integrations and cancels order`() {
        val customer = customerRepository.save(
            CustomerEntity(login = "error-user", email = "error-user@example.test", isActive = true)
        )
        val product = productRepository.save(
            ProductEntity(
                name = "compensated-product",
                productNumber = 900003,
                categoryType = CategoryType.entries.first(),
                price = BigDecimal("75.00"),
                quantity = BigDecimal("6"),
                quantityChangedDateTime = LocalDateTime.now(),
                createdDate = LocalDate.now(),
                isAvailable = true,
            )
        )
        val order = orderRepository.save(
            OrderEntity(customer = customer, deliveryAddress = "Rollback street, 7")
        )
        orderItemRepository.save(
            OrderItemEntity(
                order = order,
                product = product,
                productPrice = product.price,
                quantity = BigDecimal("2"),
            )
        )

        stubFor(post(urlEqualTo("/accounts")).willReturn(okJson("{\"error-user\":\"ACC-ERROR\"}")))
        stubFor(post(urlEqualTo("/inns")).willReturn(okJson("{\"error-user\":\"7800000000\"}")))
        stubFor(post(urlEqualTo("/contracts")).willReturn(okJson("{\"contractId\":\"CONTRACT-ERROR\"}")))
        stubFor(post(urlEqualTo("/deliveries")).willReturn(okJson("{\"deliveryDate\":\"2026-08-11\"}")))
        stubFor(post(urlEqualTo("/payments")).willReturn(serverError()))
        stubFor(delete(urlEqualTo("/contracts/CONTRACT-ERROR")).willReturn(noContent()))
        stubFor(delete(urlEqualTo("/deliveries/${order.id}")).willReturn(noContent()))
        stubFor(post(urlEqualTo("/notifications")).willReturn(aResponse().withStatus(204)))

        val businessKey = processService.start(customer.id!!, order.id!!)
        val processInstanceId = runtimeService.createProcessInstanceQuery()
            .processInstanceBusinessKey(businessKey)
            .singleResult()
            .processInstanceId

        await().atMost(Duration.ofSeconds(40)).untilAsserted {
            val cancelled = orderRepository.findById(order.id!!).orElseThrow()
            val releasedProduct = productRepository.findById(product.id!!).orElseThrow()

            assertThat(cancelled.status).isEqualTo(OrderStatusType.CANCELED)
            assertThat(cancelled.deliveryDate).isNull()
            assertThat(releasedProduct.quantity).isEqualByComparingTo("8")
            assertThat(runtimeService.createProcessInstanceQuery()
                .processInstanceId(processInstanceId)
                .count()).isZero()
        }

        assertThat(runtimeService.createIncidentQuery()
            .processInstanceId(processInstanceId)
            .count()).isZero()

        verify(postRequestedFor(urlEqualTo("/contracts")))
        verify(postRequestedFor(urlEqualTo("/deliveries")))
        verify(postRequestedFor(urlEqualTo("/payments")))
        verify(deleteRequestedFor(urlEqualTo("/contracts/CONTRACT-ERROR")))
        verify(deleteRequestedFor(urlEqualTo("/deliveries/${order.id}")))
        verify(0, postRequestedFor(urlEqualTo("/payments/${order.id}/rollback")))
        verify(postRequestedFor(urlEqualTo("/notifications"))
            .withRequestBody(matchingJsonPath("$.message", containing("Order rejected"))))
    }

    companion object {
        private val wireMock = WireMockServer(wireMockConfig().dynamicPort()).also { it.start() }

        @JvmStatic
        @DynamicPropertySource
        fun integrationProperties(registry: DynamicPropertyRegistry) {
            registry.add("rest.web-clients.account-number.base-url", wireMock::baseUrl)
            registry.add("rest.web-clients.account-number.get-account-numbers-path") { "/accounts" }
            registry.add("rest.web-clients.inn.base-url", wireMock::baseUrl)
            registry.add("rest.web-clients.inn.get-account-inns-path") { "/inns" }
            registry.add("rest.web-clients.contract.base-url", wireMock::baseUrl)
            registry.add("rest.web-clients.delivery.base-url", wireMock::baseUrl)
            registry.add("rest.web-clients.payment.base-url", wireMock::baseUrl)
            registry.add("rest.web-clients.notification.base-url", wireMock::baseUrl)
            registry.add("rest.integration-executor.core-pool-size") { 2 }
            registry.add("rest.integration-executor.max-pool-size") { 2 }
            registry.add("rest.integration-executor.queue-capacity") { 10 }
        }

        @JvmStatic
        @AfterAll
        fun stopWireMock() {
            wireMock.stop()
        }
    }
}
