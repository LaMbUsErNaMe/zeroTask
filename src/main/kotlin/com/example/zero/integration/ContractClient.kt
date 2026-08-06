package com.example.zero.integration

import com.example.zero.integration.dto.contract.ContractRegistrationRequest
import com.example.zero.integration.dto.contract.ContractRegistrationResponse
import com.example.zero.properties.RestProperties
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient

@Component
class ContractClient(
    @Qualifier("contractWebClient") webClient: WebClient,
    properties: RestProperties,
) : BaseBlockingClient(webClient) {
    private val path = properties.webClients.contract.path
    private val cancelPath = properties.webClients.contract.cancelPath

    fun register(request: ContractRegistrationRequest): ContractRegistrationResponse =
        postForResponse(
            path = path,
            request = request,
            responseType = ContractRegistrationResponse::class.java,
            emptyResponseMessage = "Contract service returned an empty response",
        )

    fun cancel(contractId: String) = delete(cancelPath, contractId)
}
