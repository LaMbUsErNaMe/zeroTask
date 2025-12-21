package com.example.zero.services

import com.example.zero.persistence.entity.ProductEntity
import com.example.zero.services.dto.ProductDto
import com.example.zero.services.dto.CreateProductServiceDto
import com.example.zero.services.dto.PatchProductServiceDto
import com.example.zero.services.dto.UpdateProductServiceDto
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

}
