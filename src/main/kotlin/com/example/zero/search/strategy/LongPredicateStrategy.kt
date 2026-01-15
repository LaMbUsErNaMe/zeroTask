package com.example.zero.search.strategy

import com.example.zero.enums.OperationType
import jakarta.persistence.criteria.CriteriaBuilder
import jakarta.persistence.criteria.Path
import jakarta.persistence.criteria.Predicate
import org.springframework.stereotype.Component
import java.lang.IllegalArgumentException

@Component
class LongPredicateStrategy : PredicateStrategy<Long> {

    override fun supports(type: Class<*>, operation: OperationType) =
        type == Long::class.java &&
                operation in setOf(
            OperationType.EQUALS,
            OperationType.GTE,
            OperationType.LTE,
            OperationType.LIKE
        )

    override fun build(
        cb: CriteriaBuilder,
        path: Path<Long>,
        value: Long,
        operation: OperationType
    ): Predicate =
        when (operation) {
            OperationType.EQUALS -> cb.equal(path, value)
            OperationType.GTE -> cb.greaterThanOrEqualTo(path, value)
            OperationType.LTE -> cb.lessThanOrEqualTo(path, value)
            OperationType.LIKE -> cb.and(cb.greaterThanOrEqualTo(path, when(value){in 0L..4L -> 0
                else -> value - 3L}), cb.lessThanOrEqualTo(path, value + 3L))
            else -> throw IllegalArgumentException("Недопустимая операция!")
        }
}
