package com.example.zero.controller

import com.example.zero.controller.dto.product.response.ResponseProductImage
import org.springframework.http.ResponseEntity
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody
import java.util.UUID

interface ProductImageController {

    fun upload(
        productId: UUID,
        files: List<MultipartFile>
    ): List<ResponseProductImage>

    fun downloadArchive(
        productId: UUID,
    ): ResponseEntity<StreamingResponseBody>
}
