package com.example.zero.kafka.handler

import com.example.zero.extension.toCreateOrderServiceDto
import com.example.zero.kafka.dto.CreateOrderKafkaEvent
import com.example.zero.kafka.dto.OrderKafkaEvent
import com.example.zero.services.OrderService
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Service

@Service
@ConditionalOnProperty(prefix = "app", name = ["kafka.enabled"], havingValue = "true", matchIfMissing = false)
class CreateOrderKafkaEventHandler(
    private val orderService: OrderService
) : OrderKafkaEventHandler {

    override fun supports(event: OrderKafkaEvent): Boolean =
        event is CreateOrderKafkaEvent

    override fun handle(event: OrderKafkaEvent) {
        val createEvent = event as CreateOrderKafkaEvent

        orderService.save(
            customerId = requireNotNull(createEvent.customerId) { "customerId must not be null" },
            request = createEvent.toCreateOrderServiceDto()
        )
    }
}
