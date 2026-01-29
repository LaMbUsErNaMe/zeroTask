package com.example.zero.controller.dto.order.response

import com.example.zero.projections.OrderItemProjection
import java.math.BigDecimal
import java.util.UUID

data class ResponseOrder (
    val orderId: UUID,
    val products: List<OrderItemProjection>,
    val totalPrice: BigDecimal
)
