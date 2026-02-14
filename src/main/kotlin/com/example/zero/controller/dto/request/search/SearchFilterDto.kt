package com.example.zero.controller.dto.request.search

import com.example.zero.enums.OperationType
import jakarta.validation.constraints.NotBlank
data class SearchFilterDto(
    @field:NotBlank
    val field: String,

    val operation: OperationType,
    @field:NotBlank
    val value: String
)
