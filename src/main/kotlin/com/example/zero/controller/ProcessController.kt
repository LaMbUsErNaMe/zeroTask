package com.example.zero.controller

import com.example.zero.controller.dto.process.ContinueOrderProcessRequest
import java.util.UUID

interface ProcessController {
    fun continueOrderConfirmation(orderId: UUID, request: ContinueOrderProcessRequest)
}
