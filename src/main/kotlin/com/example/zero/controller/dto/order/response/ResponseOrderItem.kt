package com.example.zero.controller.dto.order.response

import java.math.BigDecimal
import java.util.UUID

data class ResponseOrderItem(
    val productId: UUID,
    val productName: String,
    val quantity: BigDecimal,
    val productPrice: BigDecimal
)
