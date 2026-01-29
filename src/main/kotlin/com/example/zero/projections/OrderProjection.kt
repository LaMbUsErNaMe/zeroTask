package com.example.zero.projections

import java.util.UUID

data class OrderProjection (
    val id: UUID,
    val deliveryAddress: String,
    val customerId: UUID,
    val items: List<OrderItemProjection>
    )