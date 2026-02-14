package com.example.zero.search.strategy

import com.example.zero.enums.CategoryType
import com.example.zero.enums.OperationType
import com.example.zero.exception.ParsingException
import com.example.zero.persistence.entity.ProductEntity
import jakarta.persistence.criteria.CriteriaBuilder
import jakarta.persistence.criteria.Predicate
import jakarta.persistence.criteria.Root
import org.springframework.stereotype.Component
import java.lang.IllegalArgumentException

private val operations = setOf(
    OperationType.EQUALS
)

@Component
class EnumPredicateStrategy : PredicateStrategy<Enum<*>> {

    override fun supports(type: Class<*>, operation: OperationType) =
        type == Enum::class.java &&
                operation in operations

    override fun parseValue(rawValue: String): Enum<*> {
        return try {
            CategoryType.valueOf(rawValue)
        }catch (e: IllegalArgumentException) {
            val className = this.javaClass.typeName
            throw ParsingException("Недопустимое значение $rawValue для $className")
        }
    }

    override fun build(
        cb: CriteriaBuilder,
        root: Root<ProductEntity>,
        pathString: String,
        rawValue: String,
        operation: OperationType
    ): Predicate {
        val value = parseValue(rawValue)
        val path = root.get<Enum<*>>(pathString)
        return when (operation) {
            OperationType.EQUALS -> cb.equal(path, value)
            else -> throw IllegalArgumentException("Недопустимая операция!")
        }
    }
}
