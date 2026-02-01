package com.example.zero.projections

import java.math.BigDecimal
import java.util.UUID

data class ProductPriceProjection (
    val id: UUID,
    val price: BigDecimal,
    val quantity: BigDecimal
)