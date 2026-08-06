package com.example.zero.integration.dto.notification

import java.util.UUID

data class NotificationRequest(
    val orderId: UUID,
    val login: String,
    val message: String,
)
