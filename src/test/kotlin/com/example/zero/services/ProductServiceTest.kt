package com.example.zero.services

import com.example.zero.enums.CategoryType
import com.example.zero.exception.DuplicateException
import com.example.zero.exception.NotFoundException
import com.example.zero.extension.toProductEntity
import com.example.zero.persistence.entity.ProductEntity
import com.example.zero.persistence.repository.ProductRepository
import com.example.zero.search.ProductCriteriaPredicateBuilder
import com.example.zero.services.dto.product.CreateProductServiceDto
import com.example.zero.services.dto.product.PatchProductServiceDto
import com.example.zero.services.dto.product.UpdateProductServiceDto
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.findByIdOrNull
import org.springframework.jdbc.core.JdbcTemplate
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.UUID

class ProductServiceTest {

    private val productRepository = mockk<ProductRepository>()
    private val productCriteriaPredicateBuilder = mockk<ProductCriteriaPredicateBuilder>()
    private val jdbcTemplate = mockk<JdbcTemplate>()

    private lateinit var service: ProductServiceImpl

    @BeforeEach
    fun tetsPrepare(){
        service = ProductServiceImpl(productRepository, productCriteriaPredicateBuilder, jdbcTemplate)
    }

    @Test
    fun save_ok() {
        val dto = CreateProductServiceDto(
            name = "Product",
            productNumber = 123,
            description = null,
            categoryType = CategoryType.COMPUTERS,
            price = BigDecimal(100),
            quantity = BigDecimal(10)
        )

        val savedEntity = dto.toProductEntity().apply {
            id = UUID.randomUUID()
        }

        every { productRepository.existsByProductNumber(123) } returns false
        every { productRepository.save(any()) } returns savedEntity

        val result = service.save(dto)

        Assertions.assertEquals(savedEntity.id, result)
        verify { productRepository.save(any()) }
    }

    @Test
    fun save_dublicate_ex() {
        val dto = CreateProductServiceDto(
            name = "Product",
            productNumber = 123,
            description = null,
            categoryType = CategoryType.COMPUTERS,
            price = BigDecimal(100),
            quantity = BigDecimal(10)
        )

        every { productRepository.existsByProductNumber(123) } returns true

        assertThrows(DuplicateException::class.java) {
            service.save(dto)
        }

        verify(exactly = 0) { productRepository.save(any()) }
    }

    @Test
    fun deleteById_ex() {
        val id = UUID.randomUUID()

        every { productRepository.findByIdOrNull(id) } returns null

        assertThrows(NotFoundException::class.java) {
            service.deleteById(id)
        }
    }

    @Test
    fun deleteById_ok() {
        val id = UUID.randomUUID()
        val entity = ProductEntity(
            id = id,
            productNumber = 123,
            quantity = BigDecimal(10),
            quantityChangedDateTime = null,
            name = "Old",
            description = null,
            categoryType = CategoryType.COMPUTERS,
            price = BigDecimal(10),
            createdDate = LocalDate.now()
        )

        every { productRepository.findByIdOrNull(id) } returns entity
        every { productRepository.deleteById(id) } just Runs

        service.deleteById(id)

        verify { productRepository.deleteById(id) }
    }

    @Test
    fun findById_ok() {
        val id = UUID.randomUUID()
        val time = LocalDateTime.now()
        val date = LocalDate.now()

        val entity = ProductEntity(
            id = id,
            productNumber = 123,
            quantity = BigDecimal(10),
            quantityChangedDateTime = time,
            name = "Old",
            description = null,
            categoryType = CategoryType.COMPUTERS,
            price = BigDecimal(10),
            createdDate = date
        )

        every { productRepository.findByIdOrNull(id) } returns entity

        val result = service.findById(id)

        Assertions.assertEquals(id, result.id)
        Assertions.assertEquals(123, result.productNumber)
        Assertions.assertEquals(BigDecimal(10), result.quantity)
        Assertions.assertEquals("Old", result.name)
        Assertions.assertEquals(null, result.description)
        Assertions.assertEquals(CategoryType.COMPUTERS, result.categoryType)
        Assertions.assertEquals(BigDecimal(10), result.price)
        Assertions.assertEquals(time, result.timeStampQuantityChanged)
        Assertions.assertEquals(date, result.timeStampCreated)
    }

    @Test
    fun findAll_ok() {
        val id = UUID.randomUUID()

        val time = LocalDateTime.now()
        val date = LocalDate.now()

        val pageable = mockk<Pageable>()
        val entity = ProductEntity(
            id = id,
            productNumber = 123,
            quantity = BigDecimal(10),
            quantityChangedDateTime = time,
            name = "Old",
            description = null,
            categoryType = CategoryType.COMPUTERS,
            price = BigDecimal(10),
            createdDate = date
        )

        val page = PageImpl(listOf(entity))

        every { productRepository.findAll(pageable) } returns page

        val result = service.findAll(pageable)

        Assertions.assertEquals(1, result.content.size)

        Assertions.assertEquals(id, result.content[0].id)
        Assertions.assertEquals(123, result.content[0].productNumber)
        Assertions.assertEquals(BigDecimal(10), result.content[0].quantity)
        Assertions.assertEquals(time, result.content[0].timeStampQuantityChanged)
        Assertions.assertEquals("Old", result.content[0].name)
        Assertions.assertEquals(null, result.content[0].description)
        Assertions.assertEquals(CategoryType.COMPUTERS, result.content[0].categoryType)
        Assertions.assertEquals(BigDecimal(10), result.content[0].price)
        Assertions.assertEquals(date, result.content[0].timeStampCreated)
    }

    @Test
    fun update_quantity_changed_updates_timestamp() {
        val id = UUID.randomUUID()
        val entity = ProductEntity(
            id = id,
            productNumber = 123,
            quantity = BigDecimal(10),
            quantityChangedDateTime = null,
            name = "Old",
            description = null,
            categoryType = CategoryType.COMPUTERS,
            price = BigDecimal(10),
            createdDate = LocalDate.now()
        )

        val dto = UpdateProductServiceDto(
            name = "New",
            productNumber = 123,
            description = null,
            categoryType = CategoryType.COMPUTERS,
            price = BigDecimal(100),
            quantity = BigDecimal(5)
        )

        every { productRepository.findByIdOrNull(id) } returns entity
        every { productRepository.existsByProductNumber(any()) } returns false
        every { productRepository.save(entity) } returns entity

        service.update(id, dto)

        Assertions.assertNotNull(entity.quantityChangedDateTime)
    }

    @Test
    fun patch_duplicate_product_number() {
        val id = UUID.randomUUID()
        val entity = ProductEntity(
            id = id,
            productNumber = 123,
            quantity = BigDecimal(10),
            quantityChangedDateTime = null,
            name = "Old",
            description = null,
            categoryType = CategoryType.COMPUTERS,
            price = BigDecimal(10),
            createdDate = LocalDate.now()
        )

        val dto = PatchProductServiceDto(
            productNumber = 999,
            name = null,
            description = null,
            categoryType = null,
            price = null,
            quantity = null
        )

        every { productRepository.findByIdOrNull(id) } returns entity
        every { productRepository.existsByProductNumber(999) } returns true

        assertThrows(DuplicateException::class.java) {
            service.patch(id, dto)
        }
    }



}
