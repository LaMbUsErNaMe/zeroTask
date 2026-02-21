package com.example.zero.integration

import com.example.zero.configuration.RestProperties
import com.example.zero.exception.IntegrationException
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.bodyToMono
import reactor.core.publisher.Mono
import reactor.util.retry.Retry
import java.io.IOException
import java.time.Duration
import java.util.concurrent.CompletableFuture
import java.util.concurrent.TimeoutException

@Component
class InnClient(
    builder: WebClient.Builder,
    private val restProperties: RestProperties
) {

    private val log = LoggerFactory.getLogger(this.javaClass.name)

    private val webClient: WebClient =
        builder.baseUrl(restProperties.inn.baseUrl)
            .build()

    fun getInnsAsync(logins: List<String>): CompletableFuture<Map<String, String>> {

        return webClient.post()
            .uri(restProperties.inn.getAccountInnsPath)
            .bodyValue(logins)
            .retrieve()
            .onStatus({ it.isError }) { response ->
                response.bodyToMono<IntegrationException>()
                    .defaultIfEmpty(IntegrationException("INN service unavailable"))
                    .flatMap { error ->
                        Mono.error(
                            IntegrationException(
                                "INN service | STATUS:${response.statusCode()} " +
                                        "| EX: ${error.message}"
                            )
                        )
                    }
            }
            .bodyToMono<Map<String, String>>()
            .switchIfEmpty(Mono
                .error(IntegrationException("Empty response from INN service")))
            .map { map ->
                if (map.values.any { it.isBlank() })
                    throw IntegrationException("INN service returned blank values")
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
            .toFuture() as CompletableFuture<Map<String, String>>
    }
}
