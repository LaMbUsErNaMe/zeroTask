package com.example.zero.kafka.dto

import com.example.zero.enums.OrderStatusType
import java.util.UUID

data class UpdateOrderStatusKafkaEvent(
    override val event: String,

    val orderId: UUID? = null,

    val status: OrderStatusType? = null
) : OrderKafkaEvent
