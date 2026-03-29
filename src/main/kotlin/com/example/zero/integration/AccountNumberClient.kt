package com.example.zero.integration

import com.example.zero.properties.RestProperties
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import java.util.concurrent.CompletableFuture

@Component
class AccountNumberClient(
    accountNumberWebClient: WebClient,
    restProperties: RestProperties
) : BaseExternalMapClient(accountNumberWebClient) {

    fun getAccountNumbersFuture(logins: List<String>): CompletableFuture<Map<String, String>> =
        getValuesFuture(logins)

    suspend fun getAccountNumbersSuspended(logins: List<String>): Map<String, String> =
        getValuesSuspended(logins)

    fun getAccountNumbersBlocking(logins: List<String>): Map<String, String> =
        getValuesBlocking(logins)

    override val service: String = SERVICE

    override val requestConfig =
        ExternalClientRequestConfig(
            path = restProperties.webClients.accountNumber.getAccountNumbersPath,
            retryAttempts = restProperties.webClients.accountNumber.retryAttempts,
            minBackoff = restProperties.webClients.accountNumber.minBackoff,
            maxBackoff = restProperties.webClients.accountNumber.maxBackoff,
        )

    override val logger = Companion.logger

    private companion object {
        const val SERVICE = "ACCOUNT_NUMBER"
        val logger = KotlinLogging.logger {}
    }
}
