package com.example.zero.services

import com.example.zero.controller.dto.order.request.patch.PatchOrderRequest
import com.example.zero.controller.dto.order.request.patch.PatchOrderStatusRequest
import com.example.zero.controller.dto.order.response.ResponseOrder
import com.example.zero.persistence.entity.OrderEntity
import com.example.zero.services.dto.order.CreateOrderServiceDto
import com.example.zero.services.dto.order.PatchOrderServiceDto
import com.example.zero.services.dto.order.PatchOrderStatusServiceDto
import java.util.UUID

interface OrderService {

    fun save(customerId: Long, request: CreateOrderServiceDto): UUID

    fun findById(customerId: Long, id: UUID): ResponseOrder

    fun patch(customerId: Long, id: UUID, request: PatchOrderServiceDto)

    fun softDeleteById(customerId: Long, id: UUID)

    fun existsChekAndGetOrder(customerId: Long, id: UUID): OrderEntity

    fun confirm(customerId: Long, id: UUID)

    fun patchStatus(id: UUID, dto: PatchOrderStatusServiceDto)
}