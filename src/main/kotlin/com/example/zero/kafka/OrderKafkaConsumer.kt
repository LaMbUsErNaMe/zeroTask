package com.example.zero.kafka

import com.example.zero.kafka.dto.CreateOrderKafkaEvent
import com.example.zero.kafka.dto.DeleteOrderKafkaEvent
import com.example.zero.kafka.dto.KafkaOrderProduct
import com.example.zero.kafka.dto.OrderKafkaEvent
import com.example.zero.kafka.dto.UpdateOrderKafkaEvent
import com.example.zero.kafka.dto.UpdateOrderStatusKafkaEvent
import com.example.zero.kafka.handler.OrderKafkaEventHandler
import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.ObjectMapper
import io.github.oshai.kotlinlogging.KotlinLogging
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Service
import java.math.BigDecimal

@Service
@ConditionalOnProperty(prefix = "app", name = ["kafka.enabled"], havingValue = "true", matchIfMissing = false)
class OrderKafkaConsumer(
    private val objectMapper: ObjectMapper,
    private val handlers: List<OrderKafkaEventHandler>
) {

    @KafkaListener(
        topics = ["test_topic"],
        groupId = "\${spring.kafka.consumer.group-id}",
        containerFactory = "kafkaListenerContainerFactory"
    )
    fun listen(record: ConsumerRecord<String, ByteArray>) {
        val key = record.key()
        val partition = record.partition()
        val bytes = record.value()

        logger.info {
            "Kafka message received: key=$key, partition=$partition, consumer=$consumerInstance"
        }

        val event = try {
            objectMapper.readValue(bytes, OrderKafkaEvent::class.java)
        } catch (ex: JsonProcessingException) {
            logger.error(ex) {
                "Kafka message deserialization failed: key=$key, partition=$partition, consumer=$consumerInstance"
            }
            return
        }

        logger.info { "Kafka event deserialized: event=${event.event}, key=$key, partition=$partition" }

        val validationErrors = validate(event)
        if (validationErrors.isNotEmpty()) {
            logger.error {
                "Kafka event validation failed: event=${event.event}, key=$key, partition=$partition, " +
                        "errors=${validationErrors.joinToString("; ")}"
            }
            return
        }

        try {
            val handler = findHandler(event)
            handler.handle(event)

            logger.info {
                "Kafka event processed successfully: event=${event.event}, key=$key, partition=$partition, consumer=$consumerInstance"
            }
        } catch (ex: Exception) {
            logger.error(ex) {
                "Kafka event processing failed: key=$key, partition=$partition, consumer=$consumerInstance"
            }
            throw ex
        }
    }

    private fun validate(event: OrderKafkaEvent): List<String> =
        when (event) {
            is CreateOrderKafkaEvent -> validateCreateOrder(event)
            is UpdateOrderKafkaEvent -> validateUpdateOrder(event)
            is DeleteOrderKafkaEvent -> validateDeleteOrder(event)
            is UpdateOrderStatusKafkaEvent -> validateUpdateOrderStatus(event)
        }

    private fun validateCreateOrder(event: CreateOrderKafkaEvent): List<String> =
        buildList {
            requireNotNullField(event.customerId, "customerId")
            requireNotBlankField(event.deliveryAddress, "deliveryAddress")
            requireProducts(event.products)
        }

    private fun validateUpdateOrder(event: UpdateOrderKafkaEvent): List<String> =
        buildList {
            requireNotNullField(event.orderId, "orderId")
            requireNotNullField(event.customerId, "customerId")
            requireNotBlankField(event.deliveryAddress, "deliveryAddress")
            requireProducts(event.products)
        }

    private fun validateDeleteOrder(event: DeleteOrderKafkaEvent): List<String> =
        buildList {
            requireNotNullField(event.orderId, "orderId")
            requireNotNullField(event.customerId, "customerId")
        }

    private fun validateUpdateOrderStatus(event: UpdateOrderStatusKafkaEvent): List<String> =
        buildList {
            requireNotNullField(event.orderId, "orderId")
            requireNotNullField(event.status, "status")
        }

    private fun MutableList<String>.requireNotNullField(value: Any?, field: String) {
        if (value == null) {
            add("$field must not be null")
        }
    }

    private fun MutableList<String>.requireNotBlankField(value: String?, field: String) {
        if (value.isNullOrBlank()) {
            add("$field must not be blank")
        }
    }

    private fun MutableList<String>.requireProducts(products: List<KafkaOrderProduct>?) {
        if (products.isNullOrEmpty()) {
            add("products must not be empty")
            return
        }

        products.forEachIndexed { index, product ->
            if (product.id == null) {
                add("products[$index].id must not be null")
            }

            when {
                product.quantity == null -> add("products[$index].quantity must not be null")
                product.quantity <= BigDecimal.ZERO -> add("products[$index].quantity must be positive")
            }
        }
    }

    private fun findHandler(event: OrderKafkaEvent): OrderKafkaEventHandler =
        handlers.firstOrNull { it.supports(event) }
            ?: throw IllegalArgumentException("Unsupported kafka event: ${event.event}")

    private companion object {
        val logger = KotlinLogging.logger {}
        val consumerInstance: String =
            System.getenv("HOSTNAME")
                ?: System.getenv("COMPUTERNAME")
                ?: "local"
    }
}
