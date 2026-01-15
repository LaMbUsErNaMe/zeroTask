package com.example.zero.search.strategy

import com.example.zero.enums.OperationType
import jakarta.persistence.criteria.CriteriaBuilder
import jakarta.persistence.criteria.Path
import jakarta.persistence.criteria.Predicate
import org.springframework.stereotype.Component
import java.lang.IllegalArgumentException

@Component
class StringPredicateStrategy : PredicateStrategy<String> {

    override fun supports(type: Class<*>, operation: OperationType) =
        type == String::class.java &&
                operation in setOf(
            OperationType.EQUALS,
            OperationType.LIKE
        )

    override fun build(
        cb: CriteriaBuilder,
        path: Path<String>,
        value: String,
        operation: OperationType
    ): Predicate =
        when (operation) {
            OperationType.EQUALS -> cb.equal(path, value)
            OperationType.LIKE -> cb.like(path, "%$value%")
            else -> throw IllegalArgumentException("Недопустимая операция!")
        }
}
