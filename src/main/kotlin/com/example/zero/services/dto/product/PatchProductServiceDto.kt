package com.example.zero.services.dto.product

import com.example.zero.enums.CategoryType
import java.math.BigDecimal

data class PatchProductServiceDto(
    val name: String? = null,

    val productNumber: Long? = null,

    val description: String? = null,

    val categoryType: CategoryType? = null,

    val price: BigDecimal? = null,

    val quantity: BigDecimal? = null
)
