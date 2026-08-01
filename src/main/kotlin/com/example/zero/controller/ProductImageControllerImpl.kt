package com.example.zero.controller

import com.example.zero.controller.dto.product.response.ResponseProductImage
import com.example.zero.extension.toResponseProductImage
import com.example.zero.services.ProductImageService
import io.swagger.v3.oas.annotations.Operation
import org.springframework.http.ContentDisposition
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestPart
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody
import org.springframework.web.util.UriUtils
import java.nio.charset.StandardCharsets
import java.util.UUID

@RestController
@RequestMapping("/products/{productId}/images")
class ProductImageControllerImpl(
    private val productImageService: ProductImageService
) : ProductImageController {

    @Operation(summary = "Загрузить изображения товара")
    @PostMapping(consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    @ResponseStatus(HttpStatus.CREATED)
    override fun upload(
        @PathVariable productId: UUID,
        @RequestPart("files") files: List<MultipartFile>
    ): List<ResponseProductImage> =
        productImageService
            .upload(productId, files)
            .map { it.toResponseProductImage() }

    @Operation(
        summary = "Скачать изображения товара ZIP-архивом"
    )
    @GetMapping(
        path = ["/archive"],
        produces = ["application/zip"]
    )
    override fun downloadArchive(
        @PathVariable productId: UUID
    ): ResponseEntity<StreamingResponseBody> {
        val archive =
            productImageService.createArchive(productId)

        val utf8ArchiveName = UriUtils.encode(
            archive.archiveName,
            StandardCharsets.UTF_8
        )

        val asciiArchiveName = archive.archiveName
            .replace(Regex("[^A-Za-z0-9._-]"), "_")
            .ifBlank { "filler.zip" }

        val contentDisposition =
            "attachment; filename=\"$asciiArchiveName\"; " +
                    "filename*=UTF-8''$utf8ArchiveName"

        return ResponseEntity
            .ok()
            .contentType(
                MediaType.parseMediaType("application/zip")
            )
            .header(
                HttpHeaders.CONTENT_DISPOSITION,
                contentDisposition
            )
            .body(archive.content)
    }
}
