package com.example.zero.controller.dto.process

import jakarta.validation.constraints.AssertTrue

data class ComplianceDecisionRequest(
    val approved: Boolean,
    val reason: String? = null,
) {
    @AssertTrue(message = "Причина отказа обязательна, если compliance отклонён")
    fun isRejectionReasonValid(): Boolean = approved || !reason.isNullOrBlank()
}
