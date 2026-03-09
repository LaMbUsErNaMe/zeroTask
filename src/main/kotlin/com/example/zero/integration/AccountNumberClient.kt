package com.example.zero.integration

import com.example.zero.configuration.RestProperties
import com.example.zero.exception.EmptyResponseException
import com.example.zero.exception.InvalidResponseException
import com.example.zero.exception.RemoteServiceException
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.runBlocking
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClientRequestException
import org.springframework.web.reactive.function.client.bodyToMono
import reactor.core.publisher.Mono
import reactor.util.retry.Retry
import java.time.Duration
import java.util.concurrent.CompletableFuture

@Component
class AccountNumberClient(
    private val accountNumberWebClient: WebClient,
    private val restProperties: RestProperties
) {
    val service = "AccountNumber"

    private val log = LoggerFactory.getLogger(this.javaClass.name)

    fun getAccountNumbersFuture(logins: List<String>): CompletableFuture<Map<String, String>> =
        runCatching {
            requestAccountNumbers(logins).toFuture().thenApply { it ?: emptyMap() }
        }.onFailure {
            log.error("$service service don't respond", it)
        }.getOrThrow()

    suspend fun getAccountNumbersSuspended(logins: List<String>): Map<String, String> =
        runCatching {
            requestAccountNumbers(logins).awaitSingle()
        }.onFailure {
            log.error("$service service don't respond", it)
        }.getOrThrow()

    fun getAccountNumbersBlocking(logins: List<String>) = runBlocking { getAccountNumbersSuspended(logins) }

    private fun requestAccountNumbers(logins: List<String>): Mono<Map<String, String>> {

        return accountNumberWebClient.post()
            .uri(restProperties.webClients.accountNumber.getAccountNumbersPath)
            .bodyValue(logins)
            .retrieve()
            .onStatus({ it.isError }) { response ->
                response.bodyToMono<String>()
                    .defaultIfEmpty("unknown error")
                    .flatMap { error ->
                        Mono.error(
                            RemoteServiceException(
                                service,
                                response.statusCode().value(),
                                error
                            )
                        )
                    }
            }
            .bodyToMono<Map<String, String>>()
            .switchIfEmpty(
                Mono.error(EmptyResponseException(service))
            )
            .map { map ->
                if (map.values.any { it.isBlank() })
                    throw InvalidResponseException(
                        service,
                        "blank values returned"
                    )
                map
            }
            .retryWhen(
                Retry.backoff(restProperties.webClients.accountNumber.retryAttempts,
                    Duration.ofSeconds(restProperties.webClients.accountNumber.minBackoff))
                    .maxBackoff(Duration.ofSeconds(
                        restProperties.webClients.accountNumber.maxBackoff))
                    .filter { it is WebClientRequestException }
                    .doBeforeRetry {
                        log.error("Attempt: ${it.totalRetries() + 1}")
                    }
            )
    }
}
