package com.example.zero.controller

import com.example.zero.controller.dto.process.ComplianceDecisionRequest
import com.example.zero.orchestartor.OrderConfirmationProcessService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/process/order-confirmation")
class ProcessControllerImpl(
    private val orderConfirmationProcessService: OrderConfirmationProcessService,
) : ProcessController {

    @PostMapping("/{orderId}/compliance")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    override fun completeCompliance(
        @PathVariable orderId: UUID,
        @Valid @RequestBody request: ComplianceDecisionRequest,
    ) {
        orderConfirmationProcessService.completeCompliance(orderId, request)
    }
}
