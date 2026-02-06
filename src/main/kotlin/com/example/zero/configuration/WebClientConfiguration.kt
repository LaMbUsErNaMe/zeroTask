package com.example.zero.configuration

import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.client.WebClient

@Configuration
class WebClientConfiguration {
    @Bean
    @Qualifier("accountNumberWebClient")
    fun accountNumberWebClient(): WebClient {
        return WebClient.builder()
            .baseUrl("http://localhost:8081")
            .defaultHeader("Content-Type", "application/json")
            .build()
    }

    @Bean
    @Qualifier("innWebClient")
    fun innWebClient(): WebClient {
        return WebClient.builder()
            .baseUrl("http://localhost:8082")
            .defaultHeader("Content-Type", "application/json")
            .build()
    }
}