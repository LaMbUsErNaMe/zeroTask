package com.example.zero.exception

class RemoteServiceException (
    service: String,
    status: Int,
    message: String
) : IntegrationException("$service responded with status $status: $message")
