package com.example.zero.search.strategy

import com.example.zero.enums.OperationType
import com.example.zero.exception.ParsingException
import com.example.zero.persistence.entity.ProductEntity
import jakarta.persistence.criteria.CriteriaBuilder
import jakarta.persistence.criteria.Path
import jakarta.persistence.criteria.Predicate
import jakarta.persistence.criteria.Root
import org.springframework.stereotype.Component
import java.lang.IllegalArgumentException
import kotlin.text.get

private val operations = setOf(
    OperationType.EQUALS,
    OperationType.GTE,
    OperationType.LTE,
    OperationType.LIKE
)

@Component
class StringPredicateStrategy : PredicateStrategy<String> {

    override fun supports(type: Class<*>, operation: OperationType) =
        type == String::class.java &&
                operation in operations

    override fun parseValue(rawValue: String): String =
        try {
            rawValue.toString()
        } catch (e: IllegalArgumentException) {
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
        val path = root.get<String>(pathString)

        return when (operation) {
            OperationType.EQUALS -> cb.equal(path, rawValue)
            OperationType.LIKE -> cb.like(path, "%$rawValue%")
            OperationType.LTE -> cb.lessThanOrEqualTo(path,"%%$rawValue%")
            OperationType.GTE -> cb.greaterThanOrEqualTo(path, "%$rawValue%%")
            else -> throw IllegalArgumentException("Недопустимая операция!")
        }
    }


}
