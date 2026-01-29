package com.example.zero.projections

import java.math.BigDecimal
import java.util.UUID

data class OrderItemProjection (
    val productId: UUID,
    val productName: String,
    val quantity: BigDecimal,
    val productPrice: BigDecimal
)