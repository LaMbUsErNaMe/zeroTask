package com.example.zero.integration.dto.delivery

import java.util.UUID

data class DeliveryRegistrationRequest(
    val orderId: UUID,
    val deliveryAddress: String,
)
