package com.example.zero.services

import com.example.zero.controller.dto.order.response.OrderInfo
import com.example.zero.controller.dto.order.response.ResponseOrder
import com.example.zero.services.dto.order.ConfirmationValidationResult
import com.example.zero.services.dto.order.CreateOrderServiceDto
import com.example.zero.services.dto.order.PatchOrderServiceDto
import com.example.zero.services.dto.order.PatchOrderStatusServiceDto
import java.util.UUID

interface OrderService {

    fun save(customerId: Long, request: CreateOrderServiceDto): UUID

    fun findById(customerId: Long, id: UUID): ResponseOrder

    fun patch(customerId: Long, id: UUID, request: PatchOrderServiceDto)

    fun softDeleteById(customerId: Long, id: UUID)

    fun confirm(customerId: Long, id: UUID)

    fun checkOrderOwner(customerId: Long, id: UUID)

    fun canStartConfirmation(customerId: Long, id: UUID): Boolean

    fun markConfirmationPending(customerId: Long, id: UUID)

    fun confirmationValidation(customerId: Long, id: UUID): ConfirmationValidationResult

    fun releaseReservation(id: UUID)

    fun rejectConfirmation(id: UUID, reason: String?)

    fun patchStatus(id: UUID, dto: PatchOrderStatusServiceDto)

    fun getOrdersInfoByProduct(): Map<UUID, List<OrderInfo>>
}
