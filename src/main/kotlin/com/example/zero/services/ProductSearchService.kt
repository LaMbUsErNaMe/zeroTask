package com.example.zero.services

import com.example.zero.controller.dto.request.search.SearchFilterDto
import com.example.zero.services.dto.ProductDto
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface ProductSearchService {
    fun search(request: List<SearchFilterDto>, pageable: Pageable): Page<ProductDto>
}
