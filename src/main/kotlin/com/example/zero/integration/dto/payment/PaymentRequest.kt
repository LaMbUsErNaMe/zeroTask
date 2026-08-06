package com.example.zero.integration.dto.payment

import java.math.BigDecimal
import java.util.UUID

data class PaymentRequest(
    val orderId: UUID,
    val amount: BigDecimal,
    val accountNumber: String,
)
