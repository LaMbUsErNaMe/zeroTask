package com.example.zero.controller.dto.product.request.search

import jakarta.validation.Valid

data class SearchDto(
    @field:Valid
    val filters: List<SearchFilterDto>
)
