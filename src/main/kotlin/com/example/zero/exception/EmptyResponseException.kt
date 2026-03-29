package com.example.zero.exception

class EmptyResponseException (
    service: String
) : IntegrationException("Empty response from $service")
