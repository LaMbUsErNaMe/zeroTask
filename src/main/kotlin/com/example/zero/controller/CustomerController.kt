package com.example.zero.controller

import com.example.zero.controller.dto.product.request.CreateProductRequest
import java.util.UUID

interface CustomerController {
    fun create(dto: CreateProductRequest): UUID
}