package com.example.zero.persistence.repository

import com.example.zero.persistence.entity.ProductEntity
import com.example.zero.projections.ProductPriceProjection
import jakarta.persistence.LockModeType
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.jpa.repository.Lock
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.util.UUID

interface ProductRepository : JpaRepository<ProductEntity, UUID>, JpaSpecificationExecutor<ProductEntity> {
    fun existsByProductNumber(productNumber: Long): Boolean

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT e FROM ProductEntity e")
    fun findAllWithLock(): List<ProductEntity>
}
