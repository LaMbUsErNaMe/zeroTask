package com.example.zero.dto.request.patch

import com.example.zero.enums.Category
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Positive
import jakarta.validation.constraints.PositiveOrZero
import jakarta.validation.constraints.Size
import java.time.LocalDateTime
import java.util.UUID

@Schema(description = "DTO Для частичного обновления товара.")
data class ProductPatchRequestDto(

    @field:Schema(
        description = "Наименование",
        example = "Samsung Galaxy Note 7",
        type = "String",
    )
    val name: String?,


    @field:Schema(
        description = "Описание техники",
        example = "Характеристики телефона",
        type = "String",
    )
    val description: String?,


    @field:Schema(
        description = "A year when this car was made",
        example = "SMARTPHONES",
        type = "enum",
    )
    val category: Category?,

    @Positive
    @field:Schema(
        description = "Стоимость",
        example = "99999.99",
        type = "Double",
    )
    val price: Double?,

    @PositiveOrZero
    @field:Schema(
        description = "Кол-во",
        example = "99",
        type = "int",
    )
    val quantity: Int?,
)
