package com.example.zero.services.dto

import com.example.zero.enums.CategoryType
import java.math.BigDecimal

data class UpdateProductServiceDto(
    val name: String,

    val productNumber: Long,

    val description: String?,

    val categoryType: CategoryType,

    val price: BigDecimal,

    val quantity: BigDecimal,
)
