package com.example.zero.search.strategy

import com.example.zero.enums.OperationType
import jakarta.persistence.criteria.CriteriaBuilder
import jakarta.persistence.criteria.Path
import jakarta.persistence.criteria.Predicate
import org.springframework.stereotype.Component
import java.lang.IllegalArgumentException
import java.math.BigDecimal

@Component
class BigDecimalPredicateStrategy : PredicateStrategy<BigDecimal> {

    override fun supports(type: Class<*>, operation: OperationType) =
        type == BigDecimal::class.java &&
                operation in setOf(
            OperationType.EQUALS,
            OperationType.GTE,
            OperationType.LTE,
            OperationType.LIKE
        )

    override fun build(
        cb: CriteriaBuilder,
        path: Path<BigDecimal>,
        value: BigDecimal,
        operation: OperationType
    ): Predicate =
        when (operation) {
            OperationType.EQUALS -> cb.equal(path, value)
            OperationType.GTE -> cb.greaterThanOrEqualTo(path, value)
            OperationType.LTE -> cb.lessThanOrEqualTo(path, value)
            OperationType.LIKE -> cb.and(cb.greaterThanOrEqualTo(path, value.multiply(BigDecimal.valueOf(0.9))), cb.lessThanOrEqualTo(path, value.multiply(
                BigDecimal.valueOf(1.1))))
            else -> throw IllegalArgumentException("Недопустимая операция!")
        }
}
