package com.example.zero.kafka.handler

import com.example.zero.extension.toPatchOrderServiceDto
import com.example.zero.kafka.dto.OrderKafkaEvent
import com.example.zero.kafka.dto.UpdateOrderKafkaEvent
import com.example.zero.services.OrderService
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Service

@Service
@ConditionalOnProperty(prefix = "app", name = ["kafka.enabled"], havingValue = "true", matchIfMissing = false)
class UpdateOrderKafkaEventHandler(
    private val orderService: OrderService
) : OrderKafkaEventHandler {

    override fun supports(event: OrderKafkaEvent): Boolean =
        event is UpdateOrderKafkaEvent

    override fun handle(event: OrderKafkaEvent){
        val updateEvent = event as UpdateOrderKafkaEvent
        orderService.patch(
            customerId = requireNotNull(updateEvent.customerId),
            id = requireNotNull(updateEvent.orderId),
            request = updateEvent.toPatchOrderServiceDto()
        )
    }
}
