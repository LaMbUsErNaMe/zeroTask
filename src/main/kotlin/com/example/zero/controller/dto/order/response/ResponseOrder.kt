package com.example.zero.controller.dto.order.response

import com.example.zero.enums.OrderStatusType
import java.math.BigDecimal
import java.time.LocalDate
import java.util.UUID

data class ResponseOrder (
    val orderId: UUID,
    val products: List<ResponseOrderItem>,
    val totalPrice: BigDecimal,
    val status: OrderStatusType,
    val deliveryDate: LocalDate?
)
