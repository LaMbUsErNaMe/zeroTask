package com.example.zero.integration

import com.example.zero.integration.dto.payment.PaymentRequest
import com.example.zero.integration.dto.payment.PaymentResponse
import com.example.zero.properties.RestProperties
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import java.util.UUID

@Component
class PaymentClient(
    @Qualifier("paymentWebClient") webClient: WebClient,
    properties: RestProperties,
) : BaseBlockingClient(webClient) {
    private val path = properties.webClients.payment.path
    private val rollbackPath = properties.webClients.payment.rollbackPath

    fun pay(request: PaymentRequest): PaymentResponse =
        postForResponse(
            path = path,
            request = request,
            responseType = PaymentResponse::class.java,
            emptyResponseMessage = "Payment service returned an empty response",
        )

    fun rollback(orderId: UUID) = post(rollbackPath, orderId)
}
