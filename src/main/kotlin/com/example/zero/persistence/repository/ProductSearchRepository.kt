package com.example.zero.persistence.repository

import com.example.zero.controller.dto.request.search.SearchFilterDto
import com.example.zero.persistence.entity.ProductEntity
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface ProductSearchRepository {
    fun search(request: List<SearchFilterDto>, pageable: Pageable): Page<ProductEntity>
}
