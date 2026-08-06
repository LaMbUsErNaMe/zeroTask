package com.example.zero.kafka

import com.example.zero.kafka.dto.ComplianceRequestEvent
import com.example.zero.kafka.dto.ComplianceResultEvent
import com.fasterxml.jackson.databind.ObjectMapper
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component

@Component
@ConditionalOnProperty(prefix = "app.compliance", name = ["stub-enabled"], havingValue = "true")
class ComplianceStubKafkaListener(
    private val objectMapper: ObjectMapper,
    private val kafkaTemplate: KafkaTemplate<String, String>,
) {
    @KafkaListener(topics = [ComplianceTopics.REQUEST], groupId = "compliance-stub")
    fun listen(record: ConsumerRecord<String, ByteArray>) {
        val request = objectMapper.readValue(record.value(), ComplianceRequestEvent::class.java)
        val fraudulent = request.login.startsWith("fraud", ignoreCase = true)
        val result = ComplianceResultEvent(
            businessKey = request.businessKey,
            success = !fraudulent,
            reason = if (fraudulent) "Fraud risk detected" else null,
        )
        kafkaTemplate.send(
            ComplianceTopics.RESULT,
            request.businessKey,
            objectMapper.writeValueAsString(result),
        ).get()
    }
}
