package com.example.zero.services.dto.order

data class ConfirmationValidationResult(
    val valid: Boolean,
    val reason: String? = null,
)
