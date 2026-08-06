package com.example.zero.services

import com.example.zero.controller.dto.product.response.ProductImageArchiveDto
import com.example.zero.services.dto.product.ProductImageDto
import org.springframework.web.multipart.MultipartFile
import java.util.UUID

interface ProductImageService {

    fun upload(
        productId: UUID,
        files: List<MultipartFile>
    ): List<ProductImageDto>

    fun createArchive(
        productId: UUID
    ): ProductImageArchiveDto
}
