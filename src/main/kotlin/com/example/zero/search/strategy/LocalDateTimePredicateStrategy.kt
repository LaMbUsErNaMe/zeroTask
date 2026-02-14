package com.example.zero.search.strategy

import com.example.zero.enums.OperationType
import com.example.zero.exception.ParsingException
import com.example.zero.persistence.entity.ProductEntity
import jakarta.persistence.criteria.CriteriaBuilder
import jakarta.persistence.criteria.Predicate
import jakarta.persistence.criteria.Root
import org.springframework.stereotype.Component
import java.lang.IllegalArgumentException
import java.time.LocalDateTime
import java.time.format.DateTimeParseException

private val operations = setOf(
    OperationType.EQUALS,
    OperationType.GTE,
    OperationType.LTE,
    OperationType.LIKE
)

@Component
class LocalDateTimePredicateStrategy : PredicateStrategy<LocalDateTime> {

    override fun supports(type: Class<*>, operation: OperationType) =
        type == LocalDateTime::class.java &&
                operation in operations

    override fun parseValue(rawValue: String): LocalDateTime =
        try {
            LocalDateTime.parse(rawValue)
        } catch (e: DateTimeParseException) {
            val className = this.javaClass.typeName
            throw ParsingException("Недопустимое значение $rawValue для $className")
        }

    override fun build(
        cb: CriteriaBuilder,
        root: Root<ProductEntity>,
        pathString: String,
        rawValue: String,
        operation: OperationType
    ): Predicate {

        val value = parseValue(rawValue)

        val path = root.get<LocalDateTime>(pathString)

        return when (operation) {
            OperationType.EQUALS -> cb.equal(path, value)
            OperationType.GTE -> cb.greaterThanOrEqualTo(path, value)
            OperationType.LTE -> cb.lessThanOrEqualTo(path, value)
            OperationType.LIKE -> cb.and(cb.greaterThanOrEqualTo(path, value.minusDays(3)),
                cb.lessThanOrEqualTo(path, value.plusDays(3)))
            else -> throw IllegalArgumentException("Недопустимая операция!")
        }
    }

}