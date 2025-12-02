package com.example.zero.exception

import java.time.LocalDateTime

/**
 * Шаблон для ошибки статус сообщение время
 */

class ErrorMessageModel (
    var status: Int? = null,
    var message: String? = null,
    var timestamp: LocalDateTime = LocalDateTime.now()
)

