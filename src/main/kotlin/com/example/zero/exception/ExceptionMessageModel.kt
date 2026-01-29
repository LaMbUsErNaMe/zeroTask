package com.example.zero.exception

import java.time.LocalDateTime

/**
 * Шаблон для ошибки статус сообщение время
 */

class ExceptionMessageModel (
    var status: Int? = null,
    var message: Any? = null,
    var timestamp: LocalDateTime = LocalDateTime.now()
)
