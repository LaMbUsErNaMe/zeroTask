package com.example.zero.integration.dto.payment

data class PaymentResponse(
    val success: Boolean,
    val reason: String? = null,
)
