package com.example.zero.search.strategy

import com.example.zero.enums.OperationType
import com.example.zero.persistence.entity.ProductEntity
import jakarta.persistence.criteria.CriteriaBuilder
import jakarta.persistence.criteria.Predicate
import jakarta.persistence.criteria.Root

interface PredicateStrategy<T> {
    fun supports(type: Class<*>, operation: OperationType): Boolean

    fun parseValue(rawValue: String): T

    fun build(
        cb: CriteriaBuilder,
        root: Root<ProductEntity>,
        pathString: String,
        rawValue: String,
        operation: OperationType
    ): Predicate
}