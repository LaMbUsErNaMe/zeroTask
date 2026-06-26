package com.example.zero.extension

import com.example.zero.controller.dto.order.request.CreateOrderRequest
import com.example.zero.controller.dto.order.request.patch.PatchOrderRequest
import com.example.zero.controller.dto.order.request.patch.PatchOrderStatusRequest
import com.example.zero.controller.dto.order.response.ResponseOrderItem
import com.example.zero.projections.OrderItemProjection
import com.example.zero.services.dto.order.CreateOrderServiceDto
import com.example.zero.services.dto.order.OrderItemDto
import com.example.zero.services.dto.order.PatchOrderServiceDto
import com.example.zero.services.dto.order.PatchOrderStatusServiceDto

fun CreateOrderRequest.toCreateOrderServiceDto(customerId: Long) = CreateOrderServiceDto(
    customerId = customerId,
    deliveryAddress = deliveryAddress,
    products = items.map {
        OrderItemDto(
            productId = it.productId,
            quantity = it.quantity
        )
    }
)

fun OrderItemProjection.toResponseOrderItem() = ResponseOrderItem(
    productId = productId,
    productName = productName,
    quantity = quantity,
    productPrice = productPrice
)

fun PatchOrderRequest.toPatchOrderServiceDto(customerId: Long) = PatchOrderServiceDto(
    customerId = customerId,
    deliveryAddress = deliveryAddress,
    products = items.map {
        OrderItemDto(
            productId = it.productId,
            quantity = it.quantity
        )
    }
)

fun PatchOrderStatusRequest.toPatchOrderStatusServiceDto() = PatchOrderStatusServiceDto(
    status = status
)
