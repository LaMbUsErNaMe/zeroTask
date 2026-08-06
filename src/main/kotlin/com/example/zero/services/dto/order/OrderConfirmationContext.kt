package com.example.zero.services.dto.order

import java.math.BigDecimal
import java.util.UUID

data class OrderConfirmationContext(
    val orderId: UUID,
    val customerId: Long,
    val login: String,
    val deliveryAddress: String,
    val inn: String,
    val accountNumber: String,
    val amount: BigDecimal,
    val existingBusinessKey: String?,
)
