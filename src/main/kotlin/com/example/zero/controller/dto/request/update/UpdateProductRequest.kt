package com.example.zero.controller.dto.request.update

import com.example.zero.enums.CategoryType
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.persistence.Column
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Positive
import jakarta.validation.constraints.PositiveOrZero
import jakarta.validation.constraints.Size
import java.math.BigDecimal

@Schema(description = "DTO Для обновления товара.")
data class UpdateProductRequest(
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

    @field:NotNull(message = "Категория не может быть равна null!")
    @field:Schema(
        description = "A year when this car was made",
        example = "SMARTPHONES",
        type = "enum",
    )
    val categoryType: CategoryType,

    @field:NotNull(message = "Цена не может быть равна null!")
    @field:Positive(message = "Цена должна быть положительной и не равна нулю!")
    @field:Schema(
        description = "Стоимость",
        example = "99999.99",
        type = "BigDecimal",
    )
    val price: BigDecimal,

    @field:NotNull(message = "Кол-во не может быть равно null!")
    @field:PositiveOrZero(message = "Кол-во не может быть отрицательным!")
    @field:Schema(
        description = "Кол-во",
        example = "99",
        type = "BigDecimal",
    )
    val quantity: BigDecimal,
)
