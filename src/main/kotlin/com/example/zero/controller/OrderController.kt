package com.example.zero.controller

import com.example.zero.controller.dto.order.request.CreateOrderRequest
import com.example.zero.controller.dto.order.request.patch.PatchOrderRequest
import com.example.zero.controller.dto.order.request.patch.PatchOrderStatusRequest
import com.example.zero.controller.dto.order.response.OrderInfo
import com.example.zero.controller.dto.order.response.ResponseOrder
import jakarta.validation.Valid
import java.util.UUID

interface OrderController {
    fun create(customerId: Long, @Valid request: CreateOrderRequest): UUID

    fun patch(customerId: Long, id: UUID, @Valid request: PatchOrderRequest)

    fun confirm(customerId: Long, id: UUID)

    fun getById(customerId: Long, id: UUID): ResponseOrder

    fun delete(customerId: Long, id: UUID)

    fun patchStatus(id: UUID, dto: PatchOrderStatusRequest)

    fun getOrdersInfoByProduct(productId: UUID): Map<UUID, List<OrderInfo>>

    //fun getOrdersInfoByProduct(productId: UUID): Map<UUID товара, List<OrderInfo>>
}
