package com.example.zero.configuration

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "rest")
class RestProperties(
    val webClients: WebClients,
    val integrationExecutor: IntegrationExecutor
) {
    class WebClients(val accountNumber: AccountNumber,
                     val inn: Inn,
                     val chunkSize: Int = 1){
        class AccountNumber(
            val baseUrl: String,
            val getAccountNumbersPath: String,
            val retryAttempts: Long = 2,
            val minBackoff: Long = 1,
            val maxBackoff: Long = 5,
        )

        class Inn(
            val baseUrl: String,
            val getAccountInnsPath: String,
            val retryAttempts: Long = 2,
            val minBackoff: Long = 1,
            val maxBackoff: Long = 5,
        )
    }

    class IntegrationExecutor(
        val corePoolSize: Int = 10,
        val maxPoolSize: Int = 10,
        val queueCapacity: Int = 100
    )
}
