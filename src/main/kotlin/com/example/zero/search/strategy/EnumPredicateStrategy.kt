package com.example.zero.search.strategy

import com.example.zero.enums.OperationType
import jakarta.persistence.criteria.CriteriaBuilder
import jakarta.persistence.criteria.Path
import jakarta.persistence.criteria.Predicate
import org.springframework.stereotype.Component
import java.lang.IllegalArgumentException

@Component
class EnumPredicateStrategy : PredicateStrategy<Enum<*>> {

    override fun supports(type: Class<*>, operation: OperationType) =
        type == Enum::class.java &&
                operation in setOf(
            OperationType.EQUALS
        )

    override fun build(
        cb: CriteriaBuilder,
        path: Path<Enum<*>>,
        value: Enum<*>,
        operation: OperationType
    ): Predicate =
        when (operation) {
            OperationType.EQUALS -> cb.equal(path, value)
            else -> throw IllegalArgumentException("Недопустимая операция!")
        }
}
