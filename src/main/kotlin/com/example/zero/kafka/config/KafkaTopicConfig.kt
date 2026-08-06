package com.example.zero.kafka.config

import io.github.oshai.kotlinlogging.KotlinLogging
import org.apache.kafka.clients.admin.AdminClientConfig
import org.apache.kafka.clients.admin.NewTopic
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.core.KafkaAdmin

@Configuration
@ConditionalOnProperty(prefix = "app", name = ["kafka.enabled"], havingValue = "true", matchIfMissing = false)
class KafkaTopicConfig(
    @Value("\${spring.kafka.bootstrap-servers}")
    private val bootstrapServers: String
) {

    @Bean
    fun kafkaAdmin(): KafkaAdmin {
        logger.info { "KafkaAdmin bean created: bootstrapServers=$bootstrapServers" }
        return KafkaAdmin(mapOf(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG to bootstrapServers)).apply {
            setAutoCreate(true)
            setFatalIfBrokerNotAvailable(true)
        }
    }

    @Bean
    fun testTopic(): NewTopic {
        logger.info { "Kafka topic bean created: name=test_topic, partitions=2, replicas=1" }
        return NewTopic("test_topic", 2, 1.toShort())
    }

    private companion object {
        val logger = KotlinLogging.logger {}
    }
}
