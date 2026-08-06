package com.example.zero.properties

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "rest")
class RestProperties(
    val webClients: WebClients,
    val integrationExecutor: IntegrationExecutor
) {
    class WebClients(
        val accountNumber: AccountNumber,
        val inn: Inn,
        val contract: Contract = Contract(),
        val delivery: Delivery = Delivery(),
        val payment: Payment = Payment(),
        val notification: Notification = Notification()
    ){
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

        class Contract(
            val baseUrl: String = "http://localhost:8083",
            val path: String = "/contracts",
            val cancelPath: String = "/contracts/{contractId}",
        )

        class Delivery(
            val baseUrl: String = "http://localhost:8084",
            val path: String = "/deliveries",
            val cancelPath: String = "/deliveries/{orderId}",
        )

        class Payment(
            val baseUrl: String = "http://localhost:8087",
            val path: String = "/payments",
            val rollbackPath: String = "/payments/{orderId}/rollback",
        )

        class Notification(
            val baseUrl: String = "http://localhost:8088",
            val path: String = "/notifications",
        )
    }

    class IntegrationExecutor(
        val corePoolSize: Int = 10,
        val maxPoolSize: Int = 10,
        val queueCapacity: Int = 100
    )
}
