package com.example.zero.exception

class InvalidResponseException (
    service: String,
    message: String
) : IntegrationException("$service returned invalid data: $message")
