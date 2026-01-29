package com.example.zero.extension

import com.example.zero.controller.dto.product.request.CreateProductRequest
import com.example.zero.controller.dto.product.request.patch.PatchProductRequest
import com.example.zero.controller.dto.product.request.update.UpdateProductRequest
import com.example.zero.controller.dto.product.response.ResponseProduct
import com.example.zero.persistence.entity.ProductEntity
import com.example.zero.services.dto.product.ProductDto
import com.example.zero.services.dto.product.CreateProductServiceDto
import com.example.zero.services.dto.product.PatchProductServiceDto
import com.example.zero.services.dto.product.UpdateProductServiceDto

fun CreateProductRequest.toCreateProductServiceDto() = CreateProductServiceDto(
    name = name,
    productNumber = productNumber,
    description = description,
    categoryType = categoryType,
    price = price,
    quantity = quantity,
)

fun CreateProductServiceDto.toProductEntity() = ProductEntity(
    id = null,
    name = name,
    productNumber = productNumber,
    description = description,
    categoryType = categoryType,
    price = price,
    quantity = quantity,
    quantityChangedDateTime = null,
    createdDate = null,
)

fun UpdateProductRequest.toUpdateProductServiceDto() = UpdateProductServiceDto(
    name = name,
    productNumber = productNumber,
    description = description,
    categoryType = categoryType,
    price = price,
    quantity = quantity,
)

fun PatchProductRequest.toPatchProductServiceDto() = PatchProductServiceDto(
    name = name,
    productNumber = productNumber,
    description = description,
    categoryType = categoryType,
    price = price,
    quantity = quantity,
)

fun ProductDto.toProductResponseDto() = ResponseProduct(
    id = id,
    name = name,
    productNumber = productNumber,
    description = description,
    categoryType = categoryType,
    price = price,
    quantity = quantity,
    timeStampQuantityChanged = timeStampQuantityChanged,
    timeStampCreated = timeStampCreated
)

fun ProductEntity.toProductDto() = ProductDto(
    id = id!!,
    name = name,
    productNumber = productNumber,
    description = description,
    categoryType = categoryType,
    price = price,
    quantity = quantity,
    timeStampQuantityChanged = quantityChangedDateTime!!,
    timeStampCreated = createdDate!!,
)
