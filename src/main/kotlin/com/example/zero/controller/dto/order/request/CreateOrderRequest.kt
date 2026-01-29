package com.example.zero.controller.dto.order.request

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.NotNull
import javax.validation.Valid

@Schema(description = "DTO Для создания заказа.")
data class CreateOrderRequest(
    @field:Valid
    @field:NotEmpty
    @field:NotNull(message = "Список товаров не может быть пустым!")
    @field:Schema(
        description = "Список товаров",
        example = "",
        type = "List<CreateOrderItemRequest>"
    )
    val items: @Valid List<@Valid CreateOrderItemRequest>,

    @field:NotNull(message = "Адресс закзаа не может быть равен null!")
    @field:NotBlank(message = "Адресс заказа не может быть пустым!")
    @field:Schema(
        description = "Адресс доставки заказа",
        example = "г. Ульяновск, ул. Розы Люксембург, д. 66, кв. 777",
        type = "String"
    )
    val deliveryAddress: String
)
