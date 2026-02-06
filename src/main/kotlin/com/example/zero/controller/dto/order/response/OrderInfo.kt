package com.example.zero.controller.dto.order.response

import com.example.zero.controller.dto.customer.response.CustomerInfo
import com.example.zero.enums.OrderStatusType
import java.math.BigDecimal
import java.util.UUID

data class OrderInfo(
    val id: UUID,

    val customer: CustomerInfo,

    val status: OrderStatusType,

    val deliveryAddress: String,

    val quantity: BigDecimal
)
