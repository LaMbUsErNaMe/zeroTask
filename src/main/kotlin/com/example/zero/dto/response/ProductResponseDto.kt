package com.example.zero.dto.response

import com.example.zero.enums.Category
import io.swagger.v3.oas.annotations.media.Schema
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.UUID

@Schema(description = "DTO Для товара.")

data class ProductResponseDto(
    @field:Schema(
        description = "Идентификатор",
        example = "d0d3ef60-244f-4ddd-8061-3259931bdb20",
        type = "UUID",
    )
    val id: UUID,

    @field:Schema(
        description = "Наименование",
        example = "Samsung Galaxy Note 7",
        type = "String",
    )
    val name: String,

    @field:Schema(
        description = "Артикул",
        example = "238487923491234",
        type = "Long",
    )
    val productNumber: Long,

    @field:Schema(
        description = "Описание техники",
        example = "Характеристики телефона",
        type = "String",
    )
    val description: String,

    @field:Schema(
        description = "A year when this car was made",
        example = "SMARTPHONES",
        type = "String",
    )
    val category: Category,

    @field:Schema(
        description = "Стоимость",
        example = "9999999",
        type = "BigDecimal",
    )
    val price: BigDecimal,

    @field:Schema(
        description = "Кол-во",
        example = "99",
        type = "int",
    )
    val quantity: Int,

    @field:Schema(
        description = "Время последнего ред. кол-ва",
        example = "2025-11-30 18:58:06.174402",
        type = "LocalDateTime",
    )
    val timeStampQuantityChanged: LocalDateTime,

    @field:Schema(
        description = "Время добавления товара",
        example = "2025-11-30 18:58:06.174402",
        type = "LocalDateTime",
    )
    val timeStampCreated: LocalDateTime,
)