package com.example.zero.controller.dto.product.request.search

import com.example.zero.enums.OperationType
import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
data class SearchFilterDto(
    @field:NotBlank
    val field: String,
    @field:NotNull
    val operation: OperationType,
    @field:NotBlank
    val value: String
)
