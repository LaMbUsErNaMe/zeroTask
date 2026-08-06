package com.example.zero.integration

import org.springframework.web.reactive.function.client.WebClient

abstract class BaseBlockingClient(
    private val webClient: WebClient,
) {
    protected fun <T : Any> postForResponse(
        path: String,
        request: Any,
        responseType: Class<T>,
        emptyResponseMessage: String,
    ): T =
        webClient.post()
            .uri(path)
            .bodyValue(request)
            .retrieve()
            .bodyToMono(responseType)
            .block()
            ?: error(emptyResponseMessage)

    protected fun post(path: String, vararg uriVariables: Any) {
        webClient.post()
            .uri(path, *uriVariables)
            .retrieve()
            .toBodilessEntity()
            .block()
    }

    protected fun postBody(path: String, request: Any) {
        webClient.post()
            .uri(path)
            .bodyValue(request)
            .retrieve()
            .toBodilessEntity()
            .block()
    }

    protected fun delete(path: String, vararg uriVariables: Any) {
        webClient.delete()
            .uri(path, *uriVariables)
            .retrieve()
            .toBodilessEntity()
            .block()
    }
}
