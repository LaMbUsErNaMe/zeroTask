package com.example.zero.search

import com.example.zero.controller.dto.product.request.search.SearchFilterDto
import com.example.zero.enums.ProductFieldType
import com.example.zero.exception.ParsingException
import com.example.zero.persistence.entity.ProductEntity
import com.example.zero.search.strategy.PredicateStrategy
import jakarta.persistence.criteria.Predicate
import org.springframework.data.jpa.domain.Specification
import org.springframework.stereotype.Component

@Component
class ProductCriteriaPredicateBuilder(
    private val strategies: List<PredicateStrategy<*>>
) {
    fun build(filters: List<SearchFilterDto>): Specification<ProductEntity> =
        Specification { root, query, builder ->
            val predicates: List<Predicate> = filters.map { filter ->

                val field = ProductFieldType.from(filter.field)
                val path = field.path
                val value = filter.value

                val strategy = strategies.firstOrNull {
                    it.supports(field.type, filter.operation)
                } ?: throw ParsingException("Поле ${field.type} не поддерживается для посика или неподходящая операция ${filter.operation}")

                strategy.build(builder, root, path,value, filter.operation)
            }
            builder.and(*predicates.toTypedArray())
        }
}
