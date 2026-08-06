package com.example.zero.services.dto.order

import java.math.BigDecimal
import java.util.UUID

data class OrderItemDto(
    val productId: UUID,
    val quantity: BigDecimal
)
