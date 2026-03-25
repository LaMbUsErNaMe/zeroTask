package com.example.zero.integration

import com.example.zero.properties.RestProperties
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import java.util.concurrent.CompletableFuture

@Component
class InnClient(
    innWebClient: WebClient,
    restProperties: RestProperties
) : BaseExternalMapClient(innWebClient) {

    fun getInnsFuture(logins: List<String>): CompletableFuture<Map<String, String>> =
        getValuesFuture(logins)

    suspend fun getInnsSuspended(logins: List<String>): Map<String, String> =
        getValuesSuspended(logins)

    fun getInnsBlocking(logins: List<String>): Map<String, String> =
        getValuesBlocking(logins)

    override val service: String = SERVICE

    override val requestConfig =
        ExternalClientRequestConfig(
            path = restProperties.webClients.inn.getAccountInnsPath,
            retryAttempts = restProperties.webClients.inn.retryAttempts,
            minBackoff = restProperties.webClients.inn.minBackoff,
            maxBackoff = restProperties.webClients.inn.maxBackoff,
    )

    override val logger = Companion.logger

    private companion object {
        const val SERVICE = "INN"
        val logger = KotlinLogging.logger {}
    }
}
