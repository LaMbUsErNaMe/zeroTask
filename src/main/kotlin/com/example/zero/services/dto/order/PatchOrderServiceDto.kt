package com.example.zero.services.dto.order

import com.example.zero.controller.dto.order.request.CreateOrderItemRequest
import com.example.zero.enums.OrderStatusType

data class PatchOrderServiceDto(
    val customerId: Long,

    val deliveryAddress: String,

    val products: List<CreateOrderItemRequest>

)
