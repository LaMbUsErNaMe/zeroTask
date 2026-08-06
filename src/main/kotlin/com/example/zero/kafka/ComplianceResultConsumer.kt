package com.example.zero.kafka

import com.example.zero.kafka.dto.ComplianceResultEvent
import com.example.zero.orchestartor.model.ProcessMessages
import com.example.zero.orchestartor.model.ProcessVariables
import com.fasterxml.jackson.databind.ObjectMapper
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.camunda.bpm.engine.RuntimeService
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component

@Component
@ConditionalOnProperty(prefix = "app", name = ["kafka.enabled"], havingValue = "true")
class ComplianceResultConsumer(
    private val objectMapper: ObjectMapper,
    private val runtimeService: RuntimeService,
) {
    @KafkaListener(
        topics = [ComplianceTopics.RESULT],
        groupId = "\${spring.kafka.consumer.group-id}-compliance",
    )
    fun listen(record: ConsumerRecord<String, ByteArray>) {
        val event = objectMapper.readValue(record.value(), ComplianceResultEvent::class.java)
        runtimeService.createMessageCorrelation(ProcessMessages.COMPLIANCE_RESULT)
            .processInstanceBusinessKey(event.businessKey)
            .setVariable(ProcessVariables.COMPLIANCE_SUCCESS, event.success)
            .setVariable(ProcessVariables.REJECTION_REASON, event.reason)
            .correlate()
    }
}
