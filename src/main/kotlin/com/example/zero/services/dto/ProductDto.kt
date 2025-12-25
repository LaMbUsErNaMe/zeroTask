package com.example.zero.services.dto

import com.example.zero.enums.CategoryType
import io.swagger.v3.oas.annotations.media.Schema
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.UUID

data class ProductDto(
    val id: UUID,

    val name: String,

    val productNumber: Long,

    val description: String?,

    val categoryType: CategoryType,

    val price: BigDecimal,

    val quantity: BigDecimal,

    val timeStampQuantityChanged: LocalDateTime,

    val timeStampCreated: LocalDate,
)
