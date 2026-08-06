package com.example.zero.services.dto.order

data class CreateOrderServiceDto(

    val customerId: Long,

    val deliveryAddress: String,

    val products: List<OrderItemDto>

)
