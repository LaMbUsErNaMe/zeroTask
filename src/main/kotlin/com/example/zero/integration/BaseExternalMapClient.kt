package com.example.zero.integration

import io.github.lambusername.exceptionhandler.EmptyResponseException
import io.github.lambusername.exceptionhandler.InvalidResponseException
import io.github.lambusername.exceptionhandler.RemoteServiceException
import io.github.oshai.kotlinlogging.KLogger
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.runBlocking
import org.springframework.http.HttpStatusCode
import org.springframework.web.reactive.function.client.ClientResponse
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClientRequestException
import org.springframework.web.reactive.function.client.bodyToMono
import reactor.core.publisher.Mono
import reactor.util.retry.Retry
import java.time.Duration
import java.util.concurrent.CompletableFuture

abstract class BaseExternalMapClient(
    private val webClient: WebClient
) {

    protected abstract val service: String
    protected abstract val logger: KLogger
    protected abstract val requestConfig: ExternalClientRequestConfig

    protected fun getValuesFuture(logins: List<String>): CompletableFuture<Map<String, String>> =
        runCatching {
            requestValues(logins).toFuture().thenApply { it ?: emptyMap() }
        }.onFailure { throwable ->
            logger.error(throwable) {
                "Request to $service failed. path=${requestConfig.path}, logins=${logins.size}"
            }
        }.getOrThrow()

    protected suspend fun getValuesSuspended(logins: List<String>): Map<String, String> =
        runCatching {
            requestValues(logins).awaitSingle()
        }.onFailure { throwable ->
            logger.error(throwable) {
                "Request to $service failed. path=${requestConfig.path}, logins=${logins.size}"
            }
        }.getOrThrow()

    protected fun getValuesBlocking(logins: List<String>): Map<String, String> =
        runBlocking { getValuesSuspended(logins) }

    private fun requestValues(logins: List<String>): Mono<Map<String, String>> {
        return webClient.post()
            .uri(requestConfig.path)
            .bodyValue(logins)
            .retrieve()
            .onStatus(HttpStatusCode::isError, this::handleErrorResponse)
            .bodyToMono<Map<String, String>>()
            .switchIfEmpty(Mono.error(EmptyResponseException(service)))
            .doOnNext(this::validateResponse)
            .retryWhen(createRetrySpec())
    }

    protected open fun validateResponse(response: Map<String, String>) {
        if (response.values.any { it.isBlank() }) {
            throw InvalidResponseException(service, "Blank values returned")
        }
    }

    private fun handleErrorResponse(response: ClientResponse): Mono<Throwable> {
        return response.bodyToMono<String>()
            .defaultIfEmpty("Unknown error")
            .map { errorBody ->
                RemoteServiceException(
                    service,
                    response.statusCode().value(),
                    errorBody
                )
            }
    }

    private fun createRetrySpec(): Retry {
        return Retry.backoff(
            requestConfig.retryAttempts,
            Duration.ofSeconds(requestConfig.minBackoff)
        )
            .maxBackoff(Duration.ofSeconds(requestConfig.maxBackoff))
            .filter { it is WebClientRequestException }
            .doBeforeRetry {
                logger.error {
                    "Retry attempt #${it.totalRetries() + 1} for $service request. path=${requestConfig.path}"
                }
            }
    }
}
