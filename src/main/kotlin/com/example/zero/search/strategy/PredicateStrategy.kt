package com.example.zero.search.strategy

import com.example.zero.enums.OperationType
import jakarta.persistence.criteria.CriteriaBuilder
import jakarta.persistence.criteria.Path
import jakarta.persistence.criteria.Predicate

interface PredicateStrategy<T: Any> {
    fun supports(type: Class<*>, operation: OperationType): Boolean

    fun build(
        cb: CriteriaBuilder,
        path: Path<T>,
        value: T,
        operation: OperationType
    ): Predicate
}