package com.example.zero.extension

import com.example.zero.controller.dto.product.response.ResponseProductImage
import com.example.zero.persistence.entity.ProductImageEntity
import com.example.zero.services.dto.product.ProductImageDto

fun ProductImageDto.toResponseProductImage() =
    ResponseProductImage(
        productId = productId,
        objectKey = objectKey
    )

fun ProductImageEntity.toDto(): ProductImageDto =
    ProductImageDto(
        id = requireNotNull(id),
        objectKey = objectKey,
        originalName = originalName,
        contentType = contentType,
        size = size,
        productId = requireNotNull(product.id),
        createdAt = requireNotNull(createdAt)
    )
