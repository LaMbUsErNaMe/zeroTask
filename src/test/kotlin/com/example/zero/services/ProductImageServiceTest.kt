package com.example.zero.services

import com.example.zero.enums.CategoryType
import com.example.zero.persistence.entity.ProductEntity
import com.example.zero.persistence.entity.ProductImageEntity
import com.example.zero.persistence.repository.ProductImageRepository
import com.example.zero.persistence.repository.ProductRepository
import com.example.zero.storage.S3StorageService
import io.github.lambusername.exceptionhandler.NotFoundException
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertArrayEquals
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.data.repository.findByIdOrNull
import org.springframework.mock.web.MockMultipartFile
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.math.BigDecimal
import java.nio.charset.StandardCharsets
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.UUID
import java.util.zip.ZipInputStream

class ProductImageServiceTest {

    private val productRepository =
        mockk<ProductRepository>()

    private val productImageRepository =
        mockk<ProductImageRepository>()

    private val s3StorageService =
        mockk<S3StorageService>()

    private lateinit var service: ProductImageService

    @BeforeEach
    fun setUp() {
        service = ProductImageServiceImpl(
            productRepository = productRepository,
            productImageRepository = productImageRepository,
            s3StorageService = s3StorageService
        )
    }

    @Test
    fun `upload saves file in S3 and database`() {
        val productId = UUID.randomUUID()
        val product = createProduct(productId)
        val content = "image-content".toByteArray()

        val file = MockMultipartFile(
            "files",
            "photo.jpg",
            "image/jpeg",
            content
        )

        every {
            productRepository.findByIdOrNull(productId)
        } returns product

        every {
            s3StorageService.upload(
                objectKey = any(),
                inputStream = any(),
                contentLength = file.size,
                contentType = "image/jpeg"
            )
        } just Runs

        every {
            productImageRepository.saveAllAndFlush(
                any<List<ProductImageEntity>>()
            )
        } answers {
            firstArg<List<ProductImageEntity>>()
                .onEach { entity ->
                    entity.id = UUID.randomUUID()
                    entity.createdAt = LocalDateTime.now()
                }
        }

        val result = service.upload(
            productId,
            listOf(file)
        )

        assertEquals(1, result.size)
        assertEquals("photo.jpg", result.single().originalName)
        assertEquals("image/jpeg", result.single().contentType)
        assertEquals(content.size.toLong(), result.single().size)

        verify(exactly = 1) {
            s3StorageService.upload(
                objectKey = match {
                    it.startsWith("products/$productId/")
                },
                inputStream = any(),
                contentLength = file.size,
                contentType = "image/jpeg"
            )
        }

        verify(exactly = 1) {
            productImageRepository.saveAllAndFlush(
                any<List<ProductImageEntity>>()
            )
        }

        verify(exactly = 0) {
            s3StorageService.delete(any())
        }
    }

    @Test
    fun `upload throws when product does not exist`() {
        val productId = UUID.randomUUID()

        val file = MockMultipartFile(
            "files",
            "photo.jpg",
            "image/jpeg",
            "content".toByteArray()
        )

        every {
            productRepository.findByIdOrNull(productId)
        } returns null

        assertThrows<NotFoundException> {
            service.upload(productId, listOf(file))
        }

        verify(exactly = 0) {
            s3StorageService.upload(
                any(),
                any(),
                any(),
                any()
            )
        }

        verify(exactly = 0) {
            productImageRepository.saveAllAndFlush(
                any<List<ProductImageEntity>>()
            )
        }
    }

    @Test
    fun `upload deletes S3 objects when database save fails`() {
        val productId = UUID.randomUUID()
        val product = createProduct(productId)

        val firstFile = MockMultipartFile(
            "files",
            "first.jpg",
            "image/jpeg",
            "first-content".toByteArray()
        )

        val secondFile = MockMultipartFile(
            "files",
            "second.jpg",
            "image/jpeg",
            "second-content".toByteArray()
        )

        val uploadedKeys = mutableListOf<String>()
        val deletedKeys = mutableListOf<String>()

        every {
            productRepository.findByIdOrNull(productId)
        } returns product

        every {
            s3StorageService.upload(
                objectKey = capture(uploadedKeys),
                inputStream = any(),
                contentLength = any(),
                contentType = any()
            )
        } just Runs

        every {
            productImageRepository.saveAllAndFlush(
                any<List<ProductImageEntity>>()
            )
        } throws IllegalStateException("Database failure")

        every {
            s3StorageService.delete(
                capture(deletedKeys)
            )
        } just Runs

        assertThrows<IllegalStateException> {
            service.upload(
                productId,
                listOf(firstFile, secondFile)
            )
        }

        assertEquals(2, uploadedKeys.size)
        assertEquals(uploadedKeys.asReversed(), deletedKeys)

        verify(exactly = 2) {
            s3StorageService.delete(any())
        }
    }

    @Test
    fun `archive contains image from S3`() {
        val productId = UUID.randomUUID()
        val imageId = UUID.randomUUID()
        val product = createProduct(productId)
        val content = "image-content".toByteArray()

        val image = ProductImageEntity(
            id = imageId,
            product = product,
            objectKey = "products/$productId/$imageId",
            originalName = "photo.jpg",
            contentType = "image/jpeg",
            size = content.size.toLong(),
            createdAt = LocalDateTime.now()
        )

        every {
            productRepository.findByIdOrNull(productId)
        } returns product

        every {
            productImageRepository
                .findAllByProduct_IdOrderByCreatedAtAsc(productId)
        } returns listOf(image)

        every {
            s3StorageService.download(image.objectKey)
        } returns ByteArrayInputStream(content)

        val archive =
            service.createArchive(productId)

        assertEquals(
            "Product.zip",
            archive.archiveName
        )

        val archiveOutput = ByteArrayOutputStream()
        archive.content.writeTo(archiveOutput)

        ZipInputStream(
            ByteArrayInputStream(archiveOutput.toByteArray()),
            StandardCharsets.UTF_8
        ).use { zipInputStream ->
            val entry = zipInputStream.nextEntry

            assertEquals(
                "$imageId-photo.jpg",
                entry.name
            )

            assertArrayEquals(
                content,
                zipInputStream.readBytes()
            )

            zipInputStream.closeEntry()

            assertNull(zipInputStream.nextEntry)
        }
    }

    private fun createProduct(
        productId: UUID
    ): ProductEntity =
        ProductEntity(
            id = productId,
            name = "Product",
            productNumber = 123,
            description = null,
            categoryType = CategoryType.COMPUTERS,
            price = BigDecimal.TEN,
            quantity = BigDecimal.TEN,
            quantityChangedDateTime = LocalDateTime.now(),
            createdDate = LocalDate.now()
        )
}
