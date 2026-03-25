package com.example.zero.integration

data class ExternalClientRequestConfig(
    val path: String,
    val retryAttempts: Long,
    val minBackoff: Long,
    val maxBackoff: Long,
)
