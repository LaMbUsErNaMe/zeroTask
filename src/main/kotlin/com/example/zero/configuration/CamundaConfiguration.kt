package com.example.zero.configuration

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import org.camunda.spin.impl.json.jackson.format.JacksonJsonDataFormat
import org.camunda.spin.spi.DataFormatConfigurator
import org.springframework.boot.web.servlet.FilterRegistrationBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.Ordered
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.UrlBasedCorsConfigurationSource
import org.springframework.web.filter.CorsFilter

@Configuration
class CamundaConfiguration {
    @Bean
    fun camundaSpinJacksonConfigurator(): DataFormatConfigurator<JacksonJsonDataFormat> =
        object : DataFormatConfigurator<JacksonJsonDataFormat> {
            override fun configure(dataFormat: JacksonJsonDataFormat) {
                dataFormat.objectMapper
                    .registerKotlinModule()
                    .registerModule(JavaTimeModule())
            }

            override fun getDataFormatClass(): Class<JacksonJsonDataFormat> =
                JacksonJsonDataFormat::class.java
        }

    @Bean
    fun camundaRestCorsFilter(): FilterRegistrationBean<CorsFilter> {
        val cors = CorsConfiguration().apply {
            allowedOrigins = listOf("http://localhost:8086")
            allowedMethods = listOf("GET", "POST", "PUT", "DELETE", "OPTIONS")
            allowedHeaders = listOf("*")
        }
        val source = UrlBasedCorsConfigurationSource().apply {
            registerCorsConfiguration("/**", cors)
        }

        return FilterRegistrationBean(CorsFilter(source)).apply {
            addUrlPatterns("/engine-rest/*")
            order = Ordered.HIGHEST_PRECEDENCE
        }
    }
}
