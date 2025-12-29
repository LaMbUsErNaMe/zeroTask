package com.example.zero.search

import com.example.zero.controller.dto.request.search.SearchFilterDto
import com.example.zero.enums.OperationType
import com.example.zero.persistence.entity.ProductEntity
import jakarta.persistence.criteria.CriteriaBuilder
import jakarta.persistence.criteria.Path
import jakarta.persistence.criteria.Predicate
import jakarta.persistence.criteria.Root
import org.springframework.stereotype.Component
import java.math.BigDecimal

@Component
class ProductCriteriaPredicateBuilder {

    fun getFieldByPath(path: String): ProductField {
        return ProductField.entries.firstOrNull { it.path == path }
            ?: throw IllegalArgumentException("Unknown field: $path")
    }

    fun build(
        request: List<SearchFilterDto>,
        criteriaBuilder: CriteriaBuilder,
        root: Root<ProductEntity>
    ): List<Predicate> {

        return request.map { filter ->

            val metadata = getFieldByPath(filter.field)

            require(filter.operation in metadata.allowedOperations) {
                "Операция ${filter.operation} не разрешена для поля ${filter.field}"
            }

            val path = root.get<Any>(metadata.path)
            val value: Any = when {
                metadata.enumClass != null -> java.lang.Enum.valueOf(metadata.enumClass, filter.value.toString())

                metadata.type == BigDecimal::class.java -> when (filter.value) {
                    is Number -> BigDecimal(filter.value.toString())
                    else -> throw IllegalArgumentException("Ошибка в преобразовании ${filter.value} к BigDecimal")
                }

                metadata.type == Long::class.java -> when (filter.value) {
                    is Number -> filter.value.toLong()
                    else -> throw IllegalArgumentException("Ошибка в преобразовании ${filter.value} к Long")
                }

                metadata.type == String::class.java -> filter.value.toString()

                else -> filter.value
            }

            when (filter.operation) {
                OperationType.EQUALS -> {
                    criteriaBuilder.equal(path, value)
                }
                OperationType.GTE -> {
                    require(value is Comparable<*>) { "GTE требует Comparable" }
                    @Suppress("UNCHECKED_CAST")
                    criteriaBuilder.greaterThanOrEqualTo(path as Path<Comparable<Any>>, value as Comparable<Any>)
                }
                OperationType.LTE -> {
                    require(value is Comparable<*>) { "LTE требует Comparable" }
                    @Suppress("UNCHECKED_CAST")
                    criteriaBuilder.lessThanOrEqualTo(path as Path<Comparable<Any>>, value as Comparable<Any>)
                }
                OperationType.LIKE -> {
                    require(value is String) { "LIKE требует String" }
                    @Suppress("UNCHECKED_CAST")
                    criteriaBuilder.like(path as Path<String>, "%$value%")
                }
            }
        }
    }
}
