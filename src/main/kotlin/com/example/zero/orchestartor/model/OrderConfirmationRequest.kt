package com.example.zero.orchestartor.model

import java.io.Serializable
import java.time.LocalDateTime

data class OrderConfirmationRequest(
    val orderId: String,
    val customerId: Long,
    val requestedAt: LocalDateTime,
) : Serializable
