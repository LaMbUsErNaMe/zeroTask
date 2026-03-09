package com.example.zero.configuration

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.asCoroutineDispatcher
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor

@Configuration
class AsyncConfiguration(
    private val restProperties: RestProperties
) {
    @Bean
    fun integrationExecutor(): ThreadPoolTaskExecutor {
        val executor = ThreadPoolTaskExecutor()
        executor.corePoolSize = restProperties.integrationExecutor.corePoolSize
        executor.maxPoolSize = restProperties.integrationExecutor.maxPoolSize
        executor.queueCapacity = restProperties.integrationExecutor.queueCapacity
        executor.setThreadNamePrefix("integration-")
        executor.initialize()
        return executor
    }

    @Bean
    fun integrationDispatcher(executor: ThreadPoolTaskExecutor): CoroutineDispatcher {
        return executor.asCoroutineDispatcher()
    }
}