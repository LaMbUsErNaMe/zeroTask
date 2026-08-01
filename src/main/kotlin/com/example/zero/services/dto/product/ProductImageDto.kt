package com.example.zero.services.dto.product

import java.time.LocalDateTime
import java.util.UUID

data class ProductImageDto(
    val id: UUID,
    val objectKey: String,
    val productId: UUID,
    val originalName: String,
    val contentType: String?,
    val size: Long,
    val createdAt: LocalDateTime
)
