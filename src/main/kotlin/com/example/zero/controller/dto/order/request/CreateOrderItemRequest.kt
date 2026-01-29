package com.example.zero.controller.dto.order.request

import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Positive
import java.math.BigDecimal
import java.util.UUID

class CreateOrderItemRequest(

    @field:NotNull
    val productId: UUID,

    @field:NotNull
    @field:Positive
    val quantity: BigDecimal

)
