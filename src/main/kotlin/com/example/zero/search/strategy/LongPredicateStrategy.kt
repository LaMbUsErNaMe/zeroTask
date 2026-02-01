package com.example.zero.search.strategy

import com.example.zero.enums.OperationType
import com.example.zero.exception.ParsingException
import com.example.zero.persistence.entity.ProductEntity
import jakarta.persistence.criteria.CriteriaBuilder
import jakarta.persistence.criteria.Predicate
import jakarta.persistence.criteria.Root
import org.springframework.stereotype.Component
import java.lang.IllegalArgumentException

private val operations = setOf(
    OperationType.EQUALS,
    OperationType.GTE,
    OperationType.LTE,
    OperationType.LIKE
)

@Component
class LongPredicateStrategy : PredicateStrategy<Long> {

    override fun supports(type: Class<*>, operation: OperationType) =
        type == Long::class.java &&
                operation in operations

    override fun parseValue(rawValue: String): Long =
        try {
            rawValue.toLong()
        } catch (e: IllegalArgumentException) {
            val className = this.javaClass.name
            throw ParsingException("Недопустимое значение для парсинга $rawValue для $className")
        }

    override fun build(
        cb: CriteriaBuilder,
        root: Root<ProductEntity>,
        pathString: String,
        rawValue: String,
        operation: OperationType
    ): Predicate {
        val value = parseValue(rawValue)

        val path = root.get<Long>(pathString)

        return when (operation) {
            OperationType.EQUALS -> cb.equal(path, value)
            OperationType.GTE -> cb.greaterThanOrEqualTo(path, value)
            OperationType.LTE -> cb.lessThanOrEqualTo(path, value)
            OperationType.LIKE -> cb.and(cb.greaterThanOrEqualTo(path, value - 3L), cb.lessThanOrEqualTo(path, value + 3L))
            else -> throw IllegalArgumentException("Недопустимая операция!")
        }
    }

}
