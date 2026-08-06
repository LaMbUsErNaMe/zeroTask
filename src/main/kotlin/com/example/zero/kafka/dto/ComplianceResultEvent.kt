package com.example.zero.kafka.dto

data class ComplianceResultEvent(
    val businessKey: String,
    val success: Boolean,
    val reason: String? = null
)
