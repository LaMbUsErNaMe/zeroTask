package com.example.zero.services.dto.order

data class PatchOrderServiceDto(
    val customerId: Long,

    val deliveryAddress: String,

    val products: List<OrderItemDto>

)
