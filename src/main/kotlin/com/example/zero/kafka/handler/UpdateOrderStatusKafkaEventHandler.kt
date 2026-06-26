package com.example.zero.kafka.handler

import com.example.zero.extension.toPatchOrderStatusServiceDto
import com.example.zero.kafka.dto.OrderKafkaEvent
import com.example.zero.kafka.dto.UpdateOrderStatusKafkaEvent
import com.example.zero.services.OrderService
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Service

@Service
@ConditionalOnProperty(prefix = "app", name = ["kafka.enabled"], havingValue = "true", matchIfMissing = false)
class UpdateOrderStatusKafkaEventHandler(
    private val orderService: OrderService
) : OrderKafkaEventHandler {

    override fun supports(event: OrderKafkaEvent): Boolean =
        event is UpdateOrderStatusKafkaEvent

    override fun handle(event: OrderKafkaEvent) {
        val updateStatusEvent = event as UpdateOrderStatusKafkaEvent

        orderService.patchStatus(
            id = requireNotNull(updateStatusEvent.orderId),
            dto = updateStatusEvent.toPatchOrderStatusServiceDto()
        )
    }
}
