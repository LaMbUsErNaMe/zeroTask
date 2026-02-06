package com.example.zero.projections

import com.example.zero.enums.OrderStatusType
import java.math.BigDecimal
import java.util.UUID

data class OrderInfoProjection(
    val orderId: UUID,
    val customerId: Long,
    val customerLogin: String,
    val status: OrderStatusType,
    val deliveryAddress: String,
    val quantity: BigDecimal
)

