package com.example.zero.services.dto.order

import com.example.zero.controller.dto.order.request.CreateOrderItemRequest

data class CreateOrderServiceDto(

    val customerId: Long,

    val deliveryAddress: String,

    val products: List<CreateOrderItemRequest>

)
