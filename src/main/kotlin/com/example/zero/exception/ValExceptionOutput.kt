package com.example.zero.exception

data class ValExceptionOutput (
    var field: String,
    var message: String,
    var rejectedValue: Any?,
)
