package com.example.zero.search.strategy

import com.example.zero.enums.OperationType
import jakarta.persistence.criteria.CriteriaBuilder
import jakarta.persistence.criteria.Path
import jakarta.persistence.criteria.Predicate
import org.springframework.stereotype.Component
import java.lang.IllegalArgumentException
import java.time.LocalDate
import java.time.LocalDateTime

@Component
class LocalDateTimePredicateStrategy : PredicateStrategy<LocalDateTime> {

    override fun supports(type: Class<*>, operation: OperationType) =
        type == LocalDateTime::class.java &&
                operation in setOf(
            OperationType.EQUALS,
            OperationType.GTE,
            OperationType.LTE,
            OperationType.LIKE
        )

    override fun build(
        cb: CriteriaBuilder,
        path: Path<LocalDateTime>,
        value: LocalDateTime,
        operation: OperationType
    ): Predicate =
        when (operation) {
            OperationType.EQUALS -> cb.equal(path, value)
            OperationType.GTE -> cb.greaterThanOrEqualTo(path, value)
            OperationType.LTE -> cb.lessThanOrEqualTo(path, value)
            OperationType.LIKE -> cb.and(cb.greaterThanOrEqualTo(path, value.minusDays(3)),
                cb.lessThanOrEqualTo(path, value.plusDays(3)))
            else -> throw IllegalArgumentException("Недопустимая операция!")
        }
}