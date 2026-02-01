package com.example.zero.search.strategy

import com.example.zero.enums.OperationType
import com.example.zero.exception.ParsingException
import com.example.zero.persistence.entity.ProductEntity
import jakarta.persistence.criteria.CriteriaBuilder
import jakarta.persistence.criteria.Predicate
import jakarta.persistence.criteria.Root
import org.springframework.stereotype.Component
import java.lang.IllegalArgumentException
import java.math.BigDecimal

private val operations = setOf(
    OperationType.EQUALS,
    OperationType.GTE,
    OperationType.LTE,
    OperationType.LIKE
)

@Component
class BigDecimalPredicateStrategy : PredicateStrategy<BigDecimal> {

    override fun supports(type: Class<*>, operation: OperationType) =
        type == BigDecimal::class.java &&
                operation in operations

    override fun parseValue(rawValue: String): BigDecimal =
        try {
            rawValue.toBigDecimal()
        } catch (e: NumberFormatException) {
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
        val path = root.get<BigDecimal>(pathString)
        return when (operation) {
            OperationType.EQUALS -> cb.equal(path, value)
            OperationType.GTE -> cb.greaterThanOrEqualTo(path, value)
            OperationType.LTE -> cb.lessThanOrEqualTo(path, value)
            OperationType.LIKE ->
                cb.and(
                    cb.greaterThanOrEqualTo(
                        path, value
                            .multiply(BigDecimal.valueOf(0.9))
                    ),
                    cb.lessThanOrEqualTo(
                        path, value.multiply(
                            BigDecimal.valueOf(1.1)
                        )
                    )
                )

            else -> throw IllegalArgumentException("Недопустимая операция!")
        }
    }
}
