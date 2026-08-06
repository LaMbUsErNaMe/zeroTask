package com.example.zero.kafka.dto

import java.math.BigDecimal
import java.util.UUID

data class KafkaOrderProduct(
    val id: UUID? = null,

    val quantity: BigDecimal? = null
)
