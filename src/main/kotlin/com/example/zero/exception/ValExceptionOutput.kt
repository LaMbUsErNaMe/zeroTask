package com.example.zero.exception

import java.time.LocalDateTime

data class ValExceptionOutput (
    var field: String,
    var message: String,
    var rejectedValue: Any?,
)
