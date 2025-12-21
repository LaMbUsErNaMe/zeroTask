package com.example.zero.persistence.repository

import com.example.zero.persistence.entity.ProductEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface ProductRepository : JpaRepository<ProductEntity, UUID> {
    fun existsByProductNumber(productNumber: Long): Boolean
}
