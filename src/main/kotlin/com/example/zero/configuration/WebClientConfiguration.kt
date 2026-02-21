package com.example.zero.configuration

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.client.WebClient

@Configuration
class WebClientConfiguration {
    @Bean
    fun webClientBuilder(): WebClient.Builder =
        WebClient.builder()
            .defaultHeader("Content-Type", "application/json")
}