package com.example.zero.dto.request

import com.example.zero.enums.Category
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.persistence.Column
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Positive
import jakarta.validation.constraints.PositiveOrZero
import java.time.LocalDateTime
import java.util.UUID

@Schema(description = "DTO Для добавления товара.")
data class ProductRequestDto(
    @NotNull @NotBlank
    @field:Schema(
        description = "Наименование",
        example = "Samsung Galaxy Note 7",
        type = "String",
    )
    val name: String,

    @Column(unique = true) @NotNull
    @field:Schema(
        description = "Артикул",
        example = "238487923491234",
        type = "Long",
    )
    val productNumber: Long,

    @NotNull @NotBlank
    @field:Schema(
        description = "Описание техники",
        example = "Характеристики телефона",
        type = "String",
    )
    val description: String,

    @NotNull
    @field:Schema(
        description = "категория",
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