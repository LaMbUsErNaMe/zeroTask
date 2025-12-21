package com.example.zero.controller.dto.request

import com.example.zero.enums.CategoryType
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Positive
import jakarta.validation.constraints.PositiveOrZero
import jakarta.validation.constraints.Size
import java.math.BigDecimal

@Schema(description = "DTO Для добавления товара.")
data class CreateProductRequest(
    @field:NotNull(message = "Имя не может быть null!")
    @field:NotBlank(message = "Имя не может быть пустым!")
    @field:Schema(
        description = "Наименование",
        example = "Samsung Galaxy Note 7",
        type = "String",
    )
    val name: String,

    @field:NotNull(message = "Артикул не может быть null!")
    @field:Schema(
        description = "Артикул",
        example = "238487923491234",
        type = "Long",
    )
    val productNumber: Long,

    @field:Size(min = 1, message = "Описание не может быть пустым!")
    @field:Schema(
        description = "Описание техники",
        example = "Характеристики телефона",
        type = "String",
    )
    val description: String?,

    @field:NotNull(message = "категория не может быть null!")
    @field:Schema(
        description = "категория",
        example = "SMARTPHONES",
        type = "enum",
    )
    val categoryType: CategoryType,

    @field:NotNull(message = "Стоимость не может быть null!")
    @field:Positive(message = "Цена должна быть положительной и не равна нулю!")
    @field:Schema(
        description = "Стоимость",
        example = "99999.99",
        type = "BigDecimal",
    )
    val price: BigDecimal,

    @field:NotNull(message = "Кол-во не может быть null!")
    @field:PositiveOrZero(message = "Кол-во не может быть отрицательным!")
    @field:Schema(
        description = "Кол-во",
        example = "99",
        type = "BigDecimal",
    )
    val quantity: BigDecimal,
)
