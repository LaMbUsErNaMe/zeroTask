package com.example.zero.kafka.dto

data class ComplianceRequestEvent(
    val login: String,
    val inn: String,
    val businessKey: String
)
