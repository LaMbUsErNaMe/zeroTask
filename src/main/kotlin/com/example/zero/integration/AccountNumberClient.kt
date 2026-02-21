package com.example.zero.integration

import com.example.zero.configuration.RestProperties
import com.example.zero.exception.IntegrationException
import kotlinx.coroutines.reactor.awaitSingle
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.bodyToMono
import reactor.core.publisher.Mono
import reactor.util.retry.Retry
import java.io.IOException
import java.time.Duration
import java.util.concurrent.TimeoutException

@Component
class AccountNumberClient(
    builder: WebClient.Builder,
    private val restProperties: RestProperties
) {
    private val log = LoggerFactory.getLogger(this.javaClass.name)

    private val webClient: WebClient =
        builder.baseUrl(restProperties.accountNumber.baseUrl)
            .build()

    suspend fun getAccountNumbers(logins: List<String>): Map<String, String> =
        runCatching {
            webClient.post()
                .uri(restProperties.accountNumber.getAccountNumbersPath)
                .bodyValue(logins)
                .retrieve()
                .onStatus({ it.isError }) { response ->
                    response.bodyToMono<IntegrationException>()
                        .defaultIfEmpty(IntegrationException("AccountNumber service unavailable"))
                        .flatMap { error ->
                            Mono.error(
                                IntegrationException(
                                    "AccountNumberService | STATUS:${response.statusCode()} " +
                                            "| EX: ${error.message}"
                                )
                            )
                        }
                }
                .bodyToMono<Map<String, String>>()
                .switchIfEmpty(
                    Mono
                        .error(IntegrationException("Empty response from AccountNumber service")))
                .map { map ->
                    if (map.values.any { it.isBlank() })
                        throw IntegrationException("AccountNumber service returned blank values")
                    map
                }
                .retryWhen(
                    Retry.backoff(2, Duration.ofSeconds(1))
                        .maxBackoff(Duration.ofSeconds(5))
                        .filter { it is IOException || it is TimeoutException }
                        .doBeforeRetry {
                            log.error("Attempt: ${it.totalRetries() + 1}")
                        }
                )
                .awaitSingle()
        }.onFailure {
            log.error("AccountNumber service dont respond", it)
        }.getOrElse { ex ->
            if (ex is IntegrationException) throw ex
            throw IntegrationException("AccountNumber service unavailable $ex")
        }
}
