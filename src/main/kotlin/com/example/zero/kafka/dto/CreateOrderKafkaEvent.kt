package com.example.zero.kafka.dto

data class CreateOrderKafkaEvent(
    override val event: String,

    val customerId: Long? = null,

    val deliveryAddress: String? = null,

    val products: List<KafkaOrderProduct>? = null
) : OrderKafkaEvent
