package com.example.zero.exception

open class IntegrationException(
    message: String,
    cause: Throwable? = null)
    : RuntimeException(message, cause)
