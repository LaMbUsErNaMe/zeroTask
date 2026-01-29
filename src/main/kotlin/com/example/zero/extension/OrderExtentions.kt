package com.example.zero.extension

import com.example.zero.controller.dto.order.request.CreateOrderRequest
import com.example.zero.controller.dto.order.request.patch.PatchOrderRequest
import com.example.zero.controller.dto.order.request.patch.PatchOrderStatusRequest
import com.example.zero.services.dto.order.CreateOrderServiceDto
import com.example.zero.services.dto.order.PatchOrderServiceDto
import com.example.zero.services.dto.order.PatchOrderStatusServiceDto


fun CreateOrderRequest.toCreateOrderServiceDto(customerId: Long) = CreateOrderServiceDto(
    customerId = customerId,
    deliveryAddress = deliveryAddress,
    products = items
)

fun PatchOrderRequest.toPatchOrderServiceDto(customerId: Long) = PatchOrderServiceDto(
    customerId = customerId,
    deliveryAddress = deliveryAddress,
    products = items
)

fun PatchOrderStatusRequest.toPatchOrderStatusServiceDto() = PatchOrderStatusServiceDto(
    status = status
)
