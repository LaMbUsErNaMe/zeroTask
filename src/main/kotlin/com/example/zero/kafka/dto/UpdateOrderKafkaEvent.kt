package com.example.zero.kafka.dto

import java.util.UUID

data class UpdateOrderKafkaEvent(
    override val event: String,

    val orderId: UUID? = null,

    val customerId: Long? = null,

    val deliveryAddress: String? = null,

    val products: List<KafkaOrderProduct>? = null
) : OrderKafkaEvent
