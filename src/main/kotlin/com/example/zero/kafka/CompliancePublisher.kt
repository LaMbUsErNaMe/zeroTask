package com.example.zero.kafka

import com.example.zero.kafka.dto.ComplianceRequestEvent
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component

@Component
class CompliancePublisher(
    private val kafkaTemplate: KafkaTemplate<String, String>,
    private val objectMapper: ObjectMapper,
) {
    fun send(event: ComplianceRequestEvent) {
        kafkaTemplate.send(
            ComplianceTopics.REQUEST,
            event.businessKey,
            objectMapper.writeValueAsString(event),
        ).get()
    }
}
