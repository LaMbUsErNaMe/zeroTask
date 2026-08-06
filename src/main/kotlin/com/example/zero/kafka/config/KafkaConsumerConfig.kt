package com.example.zero.kafka.config

import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.common.serialization.ByteArrayDeserializer
import org.apache.kafka.common.serialization.StringDeserializer
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.annotation.EnableKafka
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory
import org.springframework.kafka.core.ConsumerFactory
import org.springframework.kafka.core.DefaultKafkaConsumerFactory
import org.springframework.kafka.listener.ContainerProperties

@EnableKafka
@Configuration
@ConditionalOnProperty(prefix = "app", name = ["kafka.enabled"], havingValue = "true", matchIfMissing = false)
class KafkaConsumerConfig(
    @Value("\${spring.kafka.bootstrap-servers}")
    private val bootstrapServers: String,
    @Value("\${spring.kafka.consumer.group-id}")
    private val groupId: String,
    @Value("\${spring.kafka.consumer.auto-offset-reset:earliest}")
    private val autoOffsetReset: String,
    @Value("\${spring.kafka.listener.ack-mode:record}")
    private val ackMode: String,
    @Value("\${spring.kafka.listener.concurrency:1}")
    private val concurrency: Int
) {

    @Bean
    fun consumerFactory(): ConsumerFactory<String, ByteArray> {
        val props = mutableMapOf<String, Any>(
            ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG to bootstrapServers,
            ConsumerConfig.GROUP_ID_CONFIG to groupId,
            ConsumerConfig.AUTO_OFFSET_RESET_CONFIG to autoOffsetReset,
            ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG to StringDeserializer::class.java,
            ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG to ByteArrayDeserializer::class.java
        )

        return DefaultKafkaConsumerFactory(props)
    }

    @Bean(name = ["kafkaListenerContainerFactory"])
    fun kafkaListenerContainerFactory(
        consumerFactory: ConsumerFactory<String, ByteArray>
    ): ConcurrentKafkaListenerContainerFactory<String, ByteArray> {
        val factory = ConcurrentKafkaListenerContainerFactory<String, ByteArray>()
        factory.setConsumerFactory(consumerFactory)
        factory.setConcurrency(concurrency)
        factory.containerProperties.ackMode = parseAckMode(ackMode)
        return factory
    }

    private fun parseAckMode(value: String): ContainerProperties.AckMode =
        ContainerProperties.AckMode.valueOf(value.replace("-", "_").uppercase())
}
