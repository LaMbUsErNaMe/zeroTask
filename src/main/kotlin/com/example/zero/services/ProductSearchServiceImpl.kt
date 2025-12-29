package com.example.zero.services

import com.example.zero.controller.dto.request.search.SearchFilterDto
import com.example.zero.extension.toProductDto
import com.example.zero.persistence.repository.ProductSearchRepository
import com.example.zero.services.dto.ProductDto
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service

@Service
class ProductSearchServiceImpl(
    private val productSearchRepository: ProductSearchRepository
) : ProductSearchService {
    override fun search(request: List<SearchFilterDto>,
                        pageable: Pageable
    ): Page<ProductDto> =
        productSearchRepository
            .search(request, pageable)
            .map { it.toProductDto() }
}
