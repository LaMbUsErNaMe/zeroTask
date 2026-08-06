package com.example.zero.controller.dto.product.response

import io.swagger.v3.oas.annotations.media.Schema
import java.util.UUID

@Schema(description = "Вывод юида и обджект кей")
data class ResponseProductImage(
    val productId: UUID,
    val objectKey: String
)
