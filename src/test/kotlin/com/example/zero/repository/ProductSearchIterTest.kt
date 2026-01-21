package com.example.zero.repository

import com.example.zero.controller.dto.request.search.SearchFilterDto
import com.example.zero.enums.CategoryType
import com.example.zero.enums.OperationType
import com.example.zero.persistence.entity.ProductEntity
import com.example.zero.persistence.repository.ProductRepository
import com.example.zero.search.ProductCriteriaPredicateBuilder
import com.example.zero.search.strategy.BigDecimalPredicateStrategy
import com.example.zero.search.strategy.EnumPredicateStrategy
import com.example.zero.search.strategy.LocalDatePredicateStrategy
import com.example.zero.search.strategy.LocalDateTimePredicateStrategy
import com.example.zero.search.strategy.LongPredicateStrategy
import com.example.zero.search.strategy.StringPredicateStrategy
import jakarta.persistence.EntityManager
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest
import org.springframework.context.annotation.Import
import org.springframework.test.context.ActiveProfiles
import java.math.BigDecimal

@DataJpaTest
@ActiveProfiles("test")
@Import(ProductCriteriaPredicateBuilder::class,
    BigDecimalPredicateStrategy::class,
    EnumPredicateStrategy::class,
    LocalDatePredicateStrategy::class,
    LocalDateTimePredicateStrategy::class,
    LongPredicateStrategy::class,
    StringPredicateStrategy::class)
class ProductSearchIterTest {
    @Autowired
    lateinit var productRepository: ProductRepository

    @Autowired
    lateinit var productCriteriaPredicateBuilder: ProductCriteriaPredicateBuilder

    @Autowired
    lateinit var em: EntityManager

    @BeforeEach
    fun prepare() {
        em.persist(
            ProductEntity(
                name = "Phone",
                productNumber = 1,
                price = BigDecimal("1200"),
                quantity = BigDecimal("5"),
                categoryType = CategoryType.SMARTPHONES
            )
        )

        em.persist(
            ProductEntity(
                name = "Laptop",
                productNumber = 2,
                price = BigDecimal("2000"),
                quantity = BigDecimal("3"),
                categoryType = CategoryType.LAPTOPS
            )
        )

        em.flush()
    }

    @Test
    fun `search by category`() {
        val filters = listOf(
            SearchFilterDto("categoryType", OperationType.EQUALS, "SMARTPHONES")
        )

        val spec = productCriteriaPredicateBuilder.build(filters)
        val result = productRepository.findAll(spec)

        assertEquals(1, result.size)
    }

    @Test
    fun `search by price gte`() {
        val filters = listOf(
            SearchFilterDto("price", OperationType.GTE, "1500")
        )

        val spec = productCriteriaPredicateBuilder.build(filters)
        val result = productRepository.findAll(spec)

        assertEquals(1, result.size)
    }
}