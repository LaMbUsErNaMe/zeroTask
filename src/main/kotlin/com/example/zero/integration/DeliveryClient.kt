package com.example.zero.integration

import com.example.zero.integration.dto.delivery.DeliveryRegistrationRequest
import com.example.zero.integration.dto.delivery.DeliveryRegistrationResponse
import com.example.zero.properties.RestProperties
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import java.util.UUID

@Component
class DeliveryClient(
    @Qualifier("deliveryWebClient") webClient: WebClient,
    properties: RestProperties,
) : BaseBlockingClient(webClient) {
    private val path = properties.webClients.delivery.path
    private val cancelPath = properties.webClients.delivery.cancelPath

    fun register(request: DeliveryRegistrationRequest): DeliveryRegistrationResponse =
        postForResponse(
            path = path,
            request = request,
            responseType = DeliveryRegistrationResponse::class.java,
            emptyResponseMessage = "Delivery service returned an empty response",
        )

    fun cancel(orderId: UUID) = delete(cancelPath, orderId)
}
