package com.example.zero.persistence.repository

import com.example.zero.persistence.entity.ProductImageEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface ProductImageRepository :
    JpaRepository<ProductImageEntity, UUID> {

    fun findAllByProduct_IdOrderByCreatedAtAsc(productId: UUID): List<ProductImageEntity>
}
