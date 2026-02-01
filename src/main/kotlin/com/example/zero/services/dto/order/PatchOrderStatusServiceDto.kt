package com.example.zero.services.dto.order

import com.example.zero.enums.OrderStatusType

data class PatchOrderStatusServiceDto(
    val status: OrderStatusType
)
