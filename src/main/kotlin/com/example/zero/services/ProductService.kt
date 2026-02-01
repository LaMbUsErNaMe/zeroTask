package com.example.zero.services

import com.example.zero.controller.dto.product.request.search.SearchDto
import com.example.zero.controller.dto.product.request.search.SearchFilterDto
import com.example.zero.persistence.entity.ProductEntity
import com.example.zero.services.dto.product.ProductDto
import com.example.zero.services.dto.product.CreateProductServiceDto
import com.example.zero.services.dto.product.PatchProductServiceDto
import com.example.zero.services.dto.product.UpdateProductServiceDto
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import java.util.UUID

interface ProductService {

    fun save(dto: CreateProductServiceDto): UUID

    fun deleteById(id: UUID)

    fun findById(id: UUID): ProductDto

    fun findAll(pageable: Pageable): Page<ProductDto>

    fun update(id: UUID, dto: UpdateProductServiceDto)

    fun patch(id: UUID, dto: PatchProductServiceDto)

    fun existsChekAndGetProduct(id: UUID) : ProductEntity

    fun priceUp()

    fun priceUpOpt()

    fun search(request: SearchDto, pageable: Pageable): Page<ProductDto>

}
