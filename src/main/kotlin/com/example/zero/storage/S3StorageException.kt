package com.example.zero.storage

import io.github.lambusername.exceptionhandler.IntegrationException

class S3StorageException(
    message: String,
    cause: Throwable? = null
) : IntegrationException(message, cause)
