package com.example.zero.controllers

import com.example.zero.controller.ProductController
import com.example.zero.controller.ProductControllerImpl
import com.example.zero.controller.dto.request.CreateProductRequest
import com.example.zero.enums.CategoryType
import com.example.zero.exception.GlobalExceptionControllerAdvice
import com.example.zero.services.ProductService
import com.example.zero.services.dto.ProductDto
import com.example.zero.services.dto.CreateProductServiceDto
import com.example.zero.services.dto.PatchProductServiceDto
import com.example.zero.services.dto.UpdateProductServiceDto
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.UUID

class ProductWebMockTest{

    private lateinit var mockMvc: MockMvc

    private val productService = mockk<ProductService>()

    private lateinit var controller: ProductController

    val mapper = ObjectMapper().registerKotlinModule()

    @BeforeEach
    fun tetsPrepare(){
        controller = ProductControllerImpl(productService)

        mockMvc = MockMvcBuilders
            .standaloneSetup(controller)
            .setControllerAdvice(GlobalExceptionControllerAdvice())
            .build()
    }

    @Test
    fun `post 201`() {

        val productId = UUID.randomUUID()

        val createProductServiceDto = CreateProductServiceDto(
            name = "name",
            productNumber = 123,
            description = null,
            categoryType = CategoryType.COMPUTERS,
            price = BigDecimal(1234),
            quantity = BigDecimal(99),
        )

        every { productService.save(any()) } returns productId

        mockMvc.perform(
            MockMvcRequestBuilders.post("/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(createProductServiceDto))
        )
            .andExpect(status().is2xxSuccessful)

    }

    @Test
    fun `get 200`() {

        val productId = UUID.randomUUID()

        val productDto = ProductDto(
            id = productId,
            name = "Comp",
            productNumber = 123,
            description = null,
            categoryType = CategoryType.COMPUTERS,
            price = BigDecimal(1234),
            quantity = BigDecimal(99),
            timeStampQuantityChanged = LocalDateTime.now(),
            timeStampCreated = LocalDate.now()
        )

        every { productService.findById(productId) } returns productDto

        mockMvc.perform(
            MockMvcRequestBuilders.get("/products/$productId", productId)
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(productId.toString()))
            .andExpect(jsonPath("$.name").value("Comp"))
            .andExpect(jsonPath("$.productNumber").value(123))
            .andExpect(jsonPath("$.description").isEmpty())
            .andExpect(jsonPath("$.price").value(1234))
            .andExpect(jsonPath("$.quantity").value(99))

    }

    @Test
    fun `getAll 200`() {

        val productId = UUID.randomUUID()

        val productDto = ProductDto(
            id = productId,
            name = "Comp",
            productNumber = 123,
            description = null,
            categoryType = CategoryType.COMPUTERS,
            price = BigDecimal(1234),
            quantity = BigDecimal(99),
            timeStampQuantityChanged = LocalDateTime.now(),
            timeStampCreated = LocalDate.now()
        )

        val pageable = PageRequest.of(0, 10)
        val page = PageImpl(listOf(productDto), pageable, 1)

        every { productService.findAll(any()) } returns page

        mockMvc.perform(
            MockMvcRequestBuilders.get("/products")
                .param("page", "0")
                .param("size", "10")
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.content").isArray)
            .andExpect(jsonPath("$.content[0].id").value(productId.toString()))
            .andExpect(jsonPath("$.content[0].name").value("Comp"))
            .andExpect(jsonPath("$.content[0].productNumber").value(123))
            .andExpect(jsonPath("$.content[0].price").value(1234))
            .andExpect(jsonPath("$.content[0].quantity").value(99))
            .andExpect(jsonPath("$.totalElements").value(1))
            .andExpect(jsonPath("$.totalPages").value(1))
    }

    @Test
    fun `put 200`() {

        val productId = UUID.randomUUID()

        val productDto = ProductDto(
            id = productId,
            name = "Comp",
            productNumber = 123,
            description = null,
            categoryType = CategoryType.COMPUTERS,
            price = BigDecimal(1234),
            quantity = BigDecimal(99),
            timeStampQuantityChanged = LocalDateTime.now(),
            timeStampCreated = LocalDate.now()
        )

        val updateProductServiceDto = UpdateProductServiceDto(
            name = productDto.name,
            productNumber = productDto.productNumber,
            description = productDto.description,
            categoryType = productDto.categoryType,
            price = productDto.price,
            quantity = BigDecimal("98")
        )

        every { productService.update(productId, updateProductServiceDto) } just Runs

        mockMvc.perform(MockMvcRequestBuilders.put("/products/$productId")
            .contentType(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(updateProductServiceDto)))
            .andExpect(status().isOk)

    }

    @Test
    fun `patch 200`() {

        val productId = UUID.randomUUID()

        val patchProductServiceDto = PatchProductServiceDto(
            name = null,
            productNumber = null,
            description = null,
            categoryType = null,
            price = null,
            quantity = null
        )

        every { productService.patch(productId, patchProductServiceDto) } just Runs

        mockMvc.perform(MockMvcRequestBuilders.patch("/products/$productId")
            .contentType(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(patchProductServiceDto)))
            .andExpect(status().isOk)

    }

    @Test
    fun `delete 204`() {

        val productId = UUID.randomUUID()

        every { productService.deleteById(productId) } just Runs

        mockMvc.perform(MockMvcRequestBuilders.delete("/products/$productId"))
            .andExpect(status().is2xxSuccessful)
    }

    @Test
    fun `validation CreateProductRequest`()  {
        val createProductRequest = CreateProductRequest(
            name = "",
            productNumber = 123,
            description = null,
            categoryType = CategoryType.COMPUTERS,
            price = BigDecimal(1234),
            quantity = BigDecimal(99),
        )
        mockMvc.perform(MockMvcRequestBuilders.post("/products")
            .contentType(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(createProductRequest)))
            .andExpect(status().isBadRequest)
    }

}
