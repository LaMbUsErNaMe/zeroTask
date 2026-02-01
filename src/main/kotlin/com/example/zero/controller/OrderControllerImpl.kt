package com.example.zero.controller

import com.example.zero.controller.dto.order.request.CreateOrderRequest
import com.example.zero.controller.dto.order.request.patch.PatchOrderRequest
import com.example.zero.controller.dto.order.request.patch.PatchOrderStatusRequest
import com.example.zero.controller.dto.order.response.ResponseOrder
import com.example.zero.extension.toCreateOrderServiceDto
import com.example.zero.extension.toPatchOrderServiceDto
import com.example.zero.extension.toPatchOrderStatusServiceDto
import com.example.zero.services.OrderService
import io.swagger.v3.oas.annotations.Operation
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@Validated
@RequestMapping("/order")
class OrderControllerImpl(
    private val orderService: OrderService
) : OrderController {

    @Operation(summary = "Создать заказ")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    override fun create(@RequestHeader("CustomerId") customerId: Long, @Valid @RequestBody request: CreateOrderRequest): UUID {
        return orderService.save(customerId, request.toCreateOrderServiceDto(customerId))
    }

    @Operation(summary = "Изменить заказ")
    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    override fun patch(@RequestHeader("CustomerId") customerId: Long,@PathVariable id: UUID,  @Valid @RequestBody request: PatchOrderRequest) {
        orderService.patch(
            customerId, id,request.toPatchOrderServiceDto(customerId)
        )
    }

    @Operation(summary = "Подтвердить заказ")
    @PostMapping("/{id}/status")
    @ResponseStatus(HttpStatus.CREATED)
    override fun confirm(@RequestHeader("CustomerId") customerId: Long, @PathVariable id: UUID) {
        return orderService.confirm(customerId, id)
    }

    @Operation(summary = "Получить заказ по идентификатору")
    @GetMapping("/{id}")
    override fun getById(@RequestHeader("CustomerId") customerId: Long, @PathVariable id: UUID): ResponseOrder {
        return orderService.findById(customerId, id)
    }

    @Operation(summary = "Удалить заказ")
    @DeleteMapping("/{id}") @ResponseStatus(HttpStatus.NO_CONTENT)
    override fun delete(@RequestHeader("CustomerId") customerId: Long, @PathVariable id: UUID) {
        orderService.softDeleteById(customerId, id)
    }

    @PatchMapping("/{id}/status")
    override fun patchStatus(
        @PathVariable id: UUID,@RequestBody dto: PatchOrderStatusRequest
    ) {
        orderService.patchStatus(id, dto.toPatchOrderStatusServiceDto())
    }
}