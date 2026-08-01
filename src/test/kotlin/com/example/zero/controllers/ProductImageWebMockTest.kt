package com.example.zero.controllers

import com.example.zero.controller.ProductImageControllerImpl
import com.example.zero.controller.dto.product.response.ProductImageArchiveDto
import com.example.zero.services.ProductImageService
import io.mockk.every
import io.mockk.mockk
import org.hamcrest.Matchers.containsString
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.http.HttpHeaders
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.asyncDispatch
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.header
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.request
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody
import java.util.UUID

class ProductImageWebMockTest {

    private val productImageService =
        mockk<ProductImageService>()

    private lateinit var mockMvc: MockMvc

    @BeforeEach
    fun setUp() {
        mockMvc = MockMvcBuilders
            .standaloneSetup(
                ProductImageControllerImpl(productImageService)
            )
            .build()
    }

    @Test
    fun `download returns zip with product name`() {
        val productId = UUID.randomUUID()
        val archiveBytes = "zip-content".toByteArray()

        val responseBody = StreamingResponseBody { outputStream ->
            outputStream.write(archiveBytes)
        }

        every {
            productImageService.createArchive(productId)
        } returns ProductImageArchiveDto(
            archiveName = "Phone.zip",
            content = responseBody
        )

        val asyncResult = mockMvc
            .perform(
                get("/products/$productId/images/archive")
            )
            .andExpect(status().isOk)
            .andExpect(request().asyncStarted())
            .andReturn()

        mockMvc
            .perform(asyncDispatch(asyncResult))
            .andExpect(status().isOk)
            .andExpect(
                content().contentType("application/zip")
            )
            .andExpect(
                content().bytes(archiveBytes)
            )
            .andExpect(
                header().string(
                    HttpHeaders.CONTENT_DISPOSITION,
                    containsString("filename=")
                )
            )
            .andExpect(
                header().string(
                    HttpHeaders.CONTENT_DISPOSITION,
                    containsString(
                        "filename*=UTF-8''Phone.zip"
                    )
                )
            )
    }
}
