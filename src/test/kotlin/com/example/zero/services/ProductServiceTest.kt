package com.example.zero.service

import com.example.zero.dto.request.ProductRequestDto
import com.example.zero.dto.request.update.ProductUpdateRequestDto
import com.example.zero.dto.response.ProductResponseDto
import com.example.zero.entity.Product
import com.example.zero.enums.Category
import com.example.zero.exception.NotFoundException
import com.example.zero.mapper.ProductMapper
import com.example.zero.repository.ProductRepository
import com.example.zero.services.ProductService
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.never
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.Optional
import java.util.UUID

@ExtendWith(MockitoExtension::class)
class ProductServiceTest {

    @Mock
    lateinit var productRepository: ProductRepository

    @Mock
    lateinit var productMapper: ProductMapper

    @InjectMocks
    lateinit var productService: ProductService

    @Test
    fun mapping_valid_save() {
        val dto : ProductRequestDto = ProductRequestDto(
            name = "Note 7",
            productNumber = 452345324522,
            description = "характеристики",
            category = Category.SMARTPHONES,
            price = BigDecimal.valueOf(99999.0),
            quantity = 5
        )

        val entity : Product = Product(
            id = UUID.randomUUID(),
            name = dto.name,
            productNumber = dto.productNumber,
            description = dto.description,
            category = dto.category,
            price = dto.price,
            quantity = dto.quantity,
            timeStampCreated = LocalDateTime.now(),
            timeStampQuantityChanged = LocalDateTime.now(),
        )

        val responseDto : ProductResponseDto = ProductResponseDto(
            id = entity.id!!,
            name = entity.name,
            productNumber = entity.productNumber,
            description = entity.description,
            category = entity.category,
            price = entity.price,
            quantity = entity.quantity,
            timeStampCreated = LocalDateTime.now(),
            timeStampQuantityChanged = LocalDateTime.now(),
        )

        `when`(productMapper.toEntity(dto)).thenReturn(entity)
        `when`(productRepository.save(entity)).thenReturn(entity)
        `when`(productMapper.toDto(entity)).thenReturn(responseDto)

        val result = productService.save(dto)

        assertEquals(responseDto, result)

        verify(productMapper).toEntity(dto)
        verify(productRepository).save(entity)
        verify(productMapper).toDto(entity)
    }

    @Test
    fun delete_if_exists() {
        val id = UUID.randomUUID()

        val entity : Product = Product(
            id = id,
            name = "тест",
            productNumber = 123421341235,
            description = "jgbcfgfdg",
            category = Category.SMARTPHONES,
            price = BigDecimal.valueOf(93459.99),
            quantity = 999,
            timeStampCreated = LocalDateTime.now(),
            timeStampQuantityChanged = LocalDateTime.now(),
        )
        productRepository.save<Product>(entity)

        `when`(productRepository.existsById(id)).thenReturn(true)

        productService.deleteById(id)

        verify(productRepository).existsById(id)
        verify(productRepository).deleteById(id)
    }

    @Test
    fun delete_throw_except_if_apscent() {
        val id = UUID.randomUUID()

        `when`(productRepository.existsById(id)).thenReturn(false)

        assertThrows(NotFoundException::class.java) {
            productService.deleteById(id)
        }

        verify(productRepository).existsById(id)
        verify(productRepository, never()).deleteById(id)
    }

    @Test
    fun quantity_should_change_and_time_too() {
        val id = UUID.randomUUID()

        val oldTimestamp = LocalDateTime.now().minusDays(1)

        val entity = Product(
            id = id,
            name = "Phone",
            productNumber = 123,
            description = "Desc",
            category = Category.SMARTPHONES,
            price = BigDecimal.valueOf(100.0),
            quantity = 10,
            timeStampCreated = LocalDateTime.now(),
            timeStampQuantityChanged = oldTimestamp
        )

        val dto = ProductUpdateRequestDto(
            name = "updated",
            description = "updated desc",
            category = Category.SMARTPHONES,
            price = BigDecimal.valueOf(200.0),
            quantity = 99
        )


        val saved = Product(
            id = id,
            name = dto.name,
            productNumber = entity.productNumber,
            description = dto.description,
            category = dto.category,
            price = dto.price,
            quantity = dto.quantity,
            timeStampCreated = entity.timeStampCreated,
            timeStampQuantityChanged = LocalDateTime.now()
        )

        val response = ProductResponseDto(
            id = saved.id!!,
            name = saved.name,
            productNumber = saved.productNumber,
            description = saved.description,
            category = saved.category,
            price = saved.price,
            quantity = saved.quantity,
            timeStampCreated = saved.timeStampCreated!!,
            timeStampQuantityChanged = saved.timeStampQuantityChanged!!
        )

        `when`(productRepository.findById(id)).thenReturn(Optional.of(entity))
        `when`(productRepository.save(entity)).thenReturn(saved)
        `when`(productMapper.toDto(saved)).thenReturn(response)

        val result = productService.update(id, dto)

        assertNotEquals(oldTimestamp, result.timeStampQuantityChanged)
    }

    @Test
    fun quantity_not_changed_and_time_too() {
        val id = UUID.randomUUID()

        val oldTimestamp = LocalDateTime.now()

        val entity = Product(
            id = id,
            name = "Phone",
            productNumber = 123,
            description = "Desc",
            category = Category.SMARTPHONES,
            price = BigDecimal.valueOf(100.0),
            quantity = 10,
            timeStampCreated = LocalDateTime.now(),
            timeStampQuantityChanged = oldTimestamp
        )

        val dto = ProductUpdateRequestDto(
            name = "Phone Updated",
            description = "New desc",
            category = Category.SMARTPHONES,
            price = BigDecimal.valueOf(150.0),
            quantity = 10
        )

        val saved = Product(
            id = id,
            name = dto.name,
            productNumber = entity.productNumber,
            description = dto.description,
            category = dto.category,
            price = dto.price,
            quantity = dto.quantity,
            timeStampCreated = entity.timeStampCreated,
            timeStampQuantityChanged = entity.timeStampQuantityChanged
        )

        val response = ProductResponseDto(
            id = saved.id!!,
            name = saved.name,
            productNumber = saved.productNumber,
            description = saved.description,
            category = saved.category,
            price = saved.price,
            quantity = saved.quantity,
            timeStampCreated = saved.timeStampCreated!!,
            timeStampQuantityChanged = saved.timeStampQuantityChanged!!
        )

        `when`(productRepository.findById(id)).thenReturn(Optional.of(entity))
        `when`(productRepository.save(entity)).thenReturn(saved)
        `when`(productMapper.toDto(saved)).thenReturn(response)

        val result = productService.update(id, dto)

        assertEquals(oldTimestamp, result.timeStampQuantityChanged)
    }

}
