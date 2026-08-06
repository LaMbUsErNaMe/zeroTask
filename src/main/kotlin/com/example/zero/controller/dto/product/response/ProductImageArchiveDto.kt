package com.example.zero.controller.dto.product.response

import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody

data class ProductImageArchiveDto(
    val archiveName: String,
    val content: StreamingResponseBody
)
