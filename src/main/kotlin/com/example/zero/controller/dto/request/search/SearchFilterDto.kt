package com.example.zero.controller.dto.request.search

import com.example.zero.enums.OperationType
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull

data class SearchFilterDto(
    @field:NotBlank(message = "Поле не может быть пустым!")
    val field: String,
    @field:NotNull(message = "Поле не может быть пустым!")
    val operation: OperationType,
    @field:NotNull(message = "Поле не может быть пустым!")
    val value: Any
)
