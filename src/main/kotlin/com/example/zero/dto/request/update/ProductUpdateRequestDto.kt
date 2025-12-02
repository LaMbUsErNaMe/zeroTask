package com.example.zero.dto.request.update

import com.example.zero.enums.Category
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.persistence.Column
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Positive
import jakarta.validation.constraints.PositiveOrZero
import java.time.LocalDateTime

@Schema(description = "DTO Для обновления товара.")
data class ProductUpdateRequestDto(
    @NotNull @NotBlank
    @field:Schema(
        description = "Наименование",
        example = "Samsung Galaxy Note 7",
        type = "String",
    )
    val name: String,

    @NotNull @NotBlank
    @field:Schema(
        description = "Описание техники",
        example = "Характеристики телефона",
        type = "String",
    )
    val description: String,

    @NotNull
    @field:Schema(
        description = "A year when this car was made",
        example = "SMARTPHONES",
        type = "enum",
    )
    val category: Category,

    @NotNull @Positive
    @field:Schema(
        description = "Стоимость",
        example = "99999.99",
        type = "Double",
    )
    val price: Double,

    @NotNull @PositiveOrZero
    @field:Schema(
        description = "Кол-во",
        example = "99",
        type = "int",
    )
    val quantity: Int,
)
