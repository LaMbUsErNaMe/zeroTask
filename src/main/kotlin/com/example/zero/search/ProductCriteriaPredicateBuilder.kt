package com.example.zero.search

import com.example.zero.controller.dto.request.search.SearchFilterDto
import com.example.zero.persistence.entity.ProductEntity
import com.example.zero.search.strategy.PredicateStrategy
import jakarta.persistence.criteria.CriteriaBuilder
import jakarta.persistence.criteria.Predicate
import jakarta.persistence.criteria.Root
import org.springframework.stereotype.Component
import java.lang.IllegalArgumentException
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime

@Component
class ProductCriteriaPredicateBuilder(
    private val strategies: List<PredicateStrategy<*>>
) {
    private fun convertValue(
        field: ProductField,
        rawValue: Any
    ): Any =
        when {
            field.enumClass != null ->
                java.lang.Enum.valueOf(
                    field.enumClass,
                    rawValue.toString()
                )

            field.type == LocalDate::class.java ->
                LocalDate.parse(rawValue.toString())

            field.type == LocalDateTime::class.java ->
                LocalDateTime.parse(rawValue.toString())

            field.type == BigDecimal::class.java ->
                BigDecimal(rawValue.toString())

            field.type == Long::class.java ->
                rawValue.toString().toLong()

            field.type == String::class.java ->
                rawValue.toString()

            else ->
                rawValue
        }

    fun build(
        filters: List<SearchFilterDto>,
        cb: CriteriaBuilder,
        root: Root<ProductEntity>
    ): List<Predicate> =
        filters.map { filter ->

            val field = ProductField.from(filter.field)
            val path = root.get<Any>(field.path)
            val value = convertValue(field, filter.value)

            val strategy = strategies.firstOrNull {
                it.supports(field.type,filter.operation)
            } ?: throw IllegalArgumentException("Поле ${field.type} не поддерживается для посика или неподходящая операция ${filter.operation}")

            @Suppress("UNCHECKED_CAST")
            (strategy as PredicateStrategy<Any>).build(cb, path, value, filter.operation)
        }
}

