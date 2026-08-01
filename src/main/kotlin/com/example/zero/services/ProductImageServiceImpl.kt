package com.example.zero.services

import com.example.zero.controller.dto.product.response.ProductImageArchiveDto
import com.example.zero.extension.toDto
import com.example.zero.persistence.entity.ProductImageEntity
import com.example.zero.persistence.repository.ProductImageRepository
import com.example.zero.persistence.repository.ProductRepository
import com.example.zero.services.dto.product.ProductImageDto
import com.example.zero.storage.S3StorageService
import io.github.lambusername.exceptionhandler.NotFoundException
import io.github.lambusername.exceptionhandler.ParsingException
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody
import java.nio.charset.StandardCharsets
import java.util.UUID
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

@Service
class ProductImageServiceImpl(
    private val productRepository: ProductRepository,
    private val productImageRepository: ProductImageRepository,
    private val s3StorageService: S3StorageService
) : ProductImageService {

    @Suppress("TooGenericExceptionCaught")
    override fun upload(
        productId: UUID,
        files: List<MultipartFile>
    ): List<ProductImageDto> {
        val product = productRepository.findByIdOrNull(productId)
            ?: throw NotFoundException(
                "Товар [$productId] не найден!"
            )

        validateFiles(files)

        val uploadedObjectKeys = mutableListOf<String>()

        return try {
            val entities = files.map { file ->
                val fileId = UUID.randomUUID()
                val objectKey = buildObjectKey(productId, fileId)

                file.inputStream.use { inputStream ->
                    s3StorageService.upload(
                        objectKey = objectKey,
                        inputStream = inputStream,
                        contentLength = file.size,
                        contentType = file.contentType
                    )
                }

                uploadedObjectKeys += objectKey

                ProductImageEntity(
                    product = product,
                    objectKey = objectKey,
                    originalName = resolveOriginalName(file, fileId),
                    contentType = file.contentType,
                    size = file.size
                )
            }

            productImageRepository
                .saveAllAndFlush(entities)
                .map{it.toDto()}
        } catch (exception: Exception) {
            cleanupUploadedObjects(uploadedObjectKeys)
            throw exception
        }
    }

    override fun createArchive(productId: UUID): ProductImageArchiveDto {
        val product = productRepository.findByIdOrNull(productId)
            ?: throw NotFoundException(
                "Товар [$productId] не найден!"
            )

            val images = productImageRepository
                .findAllByProduct_IdOrderByCreatedAtAsc(productId)

            if (images.isEmpty()) {
                throw NotFoundException(
                    "У товара [$productId] нет изображений"
                )
            }

        val responseBody = StreamingResponseBody { outputStream ->
            ZipOutputStream(
                outputStream,
                StandardCharsets.UTF_8
            ).use { zipOutputStream ->
                images.forEach { image ->
                    val entryName =
                        "${requireNotNull(image.id)}-${image.originalName}"

                    s3StorageService
                        .download(image.objectKey)
                        .use { inputStream ->
                            zipOutputStream.putNextEntry(
                                ZipEntry(entryName)
                            )

                            inputStream.copyTo(zipOutputStream)
                            zipOutputStream.closeEntry()
                        }
                }
            }
        }

        return ProductImageArchiveDto(
            archiveName = buildArchiveName(product.name, productId),
            content = responseBody
        )

    }

    private fun validateFiles(files: List<MultipartFile>) {
        if (files.isEmpty()) {
            throw ParsingException(
                "Не переданы файлы для загрузки"
            )
        }

        if (files.any(MultipartFile::isEmpty)) {
            throw ParsingException(
                "Нельзя загрузить пустой файл"
            )
        }
    }

    private fun buildObjectKey(
        productId: UUID,
        fileId: UUID
    ): String =
        "products/$productId/$fileId"

    private fun resolveOriginalName(
        file: MultipartFile,
        fileId: UUID
    ): String =
        file.originalFilename
            ?.replace('\\', '/')
            ?.substringAfterLast('/')
            ?.takeIf(String::isNotBlank)
            ?.take(MAX_FILE_NAME_LENGTH)
            ?: fileId.toString()

    private fun cleanupUploadedObjects(
        objectKeys: List<String>
    ) {
        objectKeys.asReversed().forEach { objectKey ->
            runCatching {
                s3StorageService.delete(objectKey)
            }.onFailure { cleanupException ->
                logger.error(cleanupException) {
                    "Failed to rollback S3 object [$objectKey]"
                }
            }
        }
    }

    private fun buildArchiveName(
        productName: String,
        productId: UUID
    ): String {
        val safeName = productName
            .replace(Regex("""[\\/:*?"<>|]"""), "_")
            .trim()
            .take(MAX_ARCHIVE_NAME_LENGTH)
            .ifBlank { "product-$productId" }

        return "$safeName.zip"
    }


    private companion object {
        const val MAX_FILE_NAME_LENGTH = 255

        const val MAX_ARCHIVE_NAME_LENGTH = 100

        val logger = KotlinLogging.logger {}
    }
}
