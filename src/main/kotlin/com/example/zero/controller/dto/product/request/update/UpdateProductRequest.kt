package com.example.zero.controller.dto.product.request.update

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
    @field:NotNull
    @field:NotBlank
    @field:Schema(
        description = "Наименование",
        example = "Samsung Galaxy Note 7",
        type = "String",
    )
    val name: String,

    @field:NotNull
    @field:Schema(
        description = "Артикул",
        example = "238487923491234",
        type = "Long",
    )
    val productNumber: Long,

    @field:Size(min = 1)
    @field:Schema(
        description = "Описание техники",
        example = "Характеристики телефона",
        type = "String",
    )
    val description: String?,

    @field:NotNull
    @field:Schema(
        description = "A year when this car was made",
        example = "SMARTPHONES",
        type = "enum",
    )
    val categoryType: CategoryType,

    @field:NotNull
    @field:Positive
    @field:Schema(
        description = "Стоимость",
        example = "99999.99",
        type = "BigDecimal",
    )
    val price: BigDecimal,

    @field:NotNull
    @field:PositiveOrZero
    @field:Schema(
        description = "Кол-во",
        example = "99",
        type = "BigDecimal",
    )
    val quantity: BigDecimal,
)
