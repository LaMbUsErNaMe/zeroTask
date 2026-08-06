package com.example.zero.controller

import com.example.zero.controller.dto.process.ComplianceDecisionRequest
import java.util.UUID

interface ProcessController {
    fun completeCompliance(orderId: UUID, request: ComplianceDecisionRequest)
}
