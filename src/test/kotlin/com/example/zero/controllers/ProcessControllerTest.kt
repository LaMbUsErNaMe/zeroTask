package com.example.zero.controllers

import com.example.zero.controller.ProcessControllerImpl
import com.example.zero.controller.dto.process.ComplianceDecisionRequest
import com.example.zero.orchestartor.OrderConfirmationProcessService
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.post
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import java.util.UUID

class ProcessControllerTest {
    private val processService = mockk<OrderConfirmationProcessService>(relaxed = true)
    private val objectMapper = jacksonObjectMapper()
    private lateinit var mockMvc: MockMvc

    @BeforeEach
    fun setUp() {
        mockMvc = MockMvcBuilders
            .standaloneSetup(ProcessControllerImpl(processService))
            .build()
    }

    @Test
    fun `approves compliance`() {
        val orderId = UUID.randomUUID()
        val request = ComplianceDecisionRequest(approved = true)

        mockMvc.post("/process/order-confirmation/$orderId/compliance") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(request)
        }.andExpect {
            status { isNoContent() }
        }

        verify(exactly = 1) { processService.completeCompliance(orderId, request) }
    }

    @Test
    fun `rejects compliance`() {
        val orderId = UUID.randomUUID()
        val request = ComplianceDecisionRequest(
            approved = false,
            reason = "Fraud risk detected",
        )

        mockMvc.post("/process/order-confirmation/$orderId/compliance") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(request)
        }.andExpect {
            status { isNoContent() }
        }

        verify(exactly = 1) { processService.completeCompliance(orderId, request) }
    }

    @Test
    fun `requires reason for rejected compliance`() {
        val orderId = UUID.randomUUID()

        mockMvc.post("/process/order-confirmation/$orderId/compliance") {
            contentType = MediaType.APPLICATION_JSON
            content = """{"approved":false}"""
        }.andExpect {
            status { isBadRequest() }
        }

        verify(exactly = 0) {
            processService.completeCompliance(orderId, any())
        }
    }
}
