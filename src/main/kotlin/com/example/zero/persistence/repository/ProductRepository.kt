package com.example.zero.persistence.repository

import com.example.zero.persistence.entity.ProductEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface ProductRepository : JpaRepository<ProductEntity, UUID> {
    fun existsByProductNumber(productNumber: Long): Boolean
}
/*
 * Parsing query method names is divided into subject and predicate.
 * The first part (find…By, exists…By) defines the subject of the query, the second part forms the predicate.
 * The introducing clause (subject) can contain further expressions.
 * Any text between find (or other introducing keywords) and By is considered to be descriptive unless using one of
 * the result-limiting keywords such as a Distinct to set a distinct flag on the query to be created or
 * Top/First to limit query results.
 */
