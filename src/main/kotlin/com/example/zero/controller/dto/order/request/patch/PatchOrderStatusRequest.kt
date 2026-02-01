package com.example.zero.controller.dto.order.request.patch

import com.example.zero.enums.OrderStatusType
import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "DTO Для частичного обновления заказа.")
data class PatchOrderStatusRequest(

    @field:Schema(
        description = "",
        example = "",
        type = ""
    )
    val status: OrderStatusType,
)
