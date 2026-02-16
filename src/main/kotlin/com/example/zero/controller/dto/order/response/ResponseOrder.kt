package com.example.zero.controller.dto.order.response

import java.math.BigDecimal
import java.util.UUID

data class ResponseOrder (
    val orderId: UUID,
    val products: List<ResponseOrderItem>,
    val totalPrice: BigDecimal
)
