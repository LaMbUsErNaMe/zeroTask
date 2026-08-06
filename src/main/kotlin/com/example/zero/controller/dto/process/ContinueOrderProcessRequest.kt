package com.example.zero.controller.dto.process

import jakarta.validation.constraints.AssertTrue

data class ContinueOrderProcessRequest(
    val success: Boolean,
    val reason: String? = null,
) {
    @AssertTrue(message = "Причина отказа обязательна, если подтверждение не прошло")
    fun isRejectionReasonValid(): Boolean = success || !reason.isNullOrBlank()
}
