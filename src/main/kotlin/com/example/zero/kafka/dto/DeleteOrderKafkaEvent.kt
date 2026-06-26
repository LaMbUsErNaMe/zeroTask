package com.example.zero.kafka.dto

import java.util.UUID

data class DeleteOrderKafkaEvent(
    override val event: String,

    val orderId: UUID? = null,

    val customerId: Long? = null
) : OrderKafkaEvent
