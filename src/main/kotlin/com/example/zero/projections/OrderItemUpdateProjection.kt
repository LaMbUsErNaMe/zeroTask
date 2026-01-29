package com.example.zero.projections

import java.math.BigDecimal
import java.util.UUID

data class OrderItemUpdateProjection(
    val productId: UUID,
    val quantity: BigDecimal,
    val productPrice: BigDecimal,
    val id: UUID
)
