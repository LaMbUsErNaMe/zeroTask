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
class InnClient(
    private val innWebClient: WebClient,
    private val restProperties: RestProperties
) {
    val service = "INN"

    private val log = LoggerFactory.getLogger(this.javaClass.name)

    fun getInnsFuture(logins: List<String>): CompletableFuture<Map<String, String>> =
        runCatching {
            requestInns(logins).toFuture().thenApply { it ?: emptyMap() }
        }.onFailure {
            log.error("$service service dont respond", it)
        }.getOrThrow()

    suspend fun getInnsSuspended(logins: List<String>): Map<String, String> =
        runCatching {
            requestInns(logins).awaitSingle()
        }.onFailure {
            log.error("$service service dont respond", it)
        }.getOrThrow()

    fun getInnsBlocking(logins: List<String>) = runBlocking { getInnsSuspended(logins) }

    private fun requestInns(logins: List<String>): Mono<Map<String, String>> {

        return innWebClient.post()
            .uri(restProperties.webClients.inn.getAccountInnsPath)
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
                Retry.backoff(restProperties.webClients.inn.retryAttempts,
                    Duration.ofSeconds(restProperties.webClients.inn.minBackoff))
                    .maxBackoff(Duration.ofSeconds(restProperties.webClients.inn.maxBackoff))
                    .filter { it is WebClientRequestException }
                    .doBeforeRetry {
                        log.error("Attempt: ${it.totalRetries() + 1}")
                    }
            )
    }
}

