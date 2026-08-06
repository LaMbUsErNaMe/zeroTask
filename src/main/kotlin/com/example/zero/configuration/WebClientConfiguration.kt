package com.example.zero.configuration

import com.example.zero.properties.RestProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.client.WebClient

@Configuration
class WebClientConfiguration(private val restProperties: RestProperties) {
    @Bean
    fun webClientBuilder(): WebClient.Builder = WebClient.builder()

    @Bean
    fun accountNumberWebClient(builder: WebClient.Builder): WebClient =
        builder
            .baseUrl(restProperties.webClients.accountNumber.baseUrl)
            .defaultHeader("Content-Type", "application/json")
            .build()

    @Bean
    fun innWebClient(builder: WebClient.Builder): WebClient =
        builder
            .baseUrl(restProperties.webClients.inn.baseUrl)
            .defaultHeader("Content-Type", "application/json")
            .build()

    @Bean
    fun contractWebClient(builder: WebClient.Builder): WebClient =
        builder
            .baseUrl(restProperties.webClients.contract.baseUrl)
            .defaultHeader("Content-Type", "application/json")
            .build()

    @Bean
    fun deliveryWebClient(builder: WebClient.Builder): WebClient =
        builder
            .baseUrl(restProperties.webClients.delivery.baseUrl)
            .defaultHeader("Content-Type", "application/json")
            .build()

    @Bean
    fun paymentWebClient(builder: WebClient.Builder): WebClient =
        builder
            .baseUrl(restProperties.webClients.payment.baseUrl)
            .defaultHeader("Content-Type", "application/json")
            .build()

    @Bean
    fun notificationWebClient(builder: WebClient.Builder): WebClient =
        builder
            .baseUrl(restProperties.webClients.notification.baseUrl)
            .defaultHeader("Content-Type", "application/json")
            .build()
}
