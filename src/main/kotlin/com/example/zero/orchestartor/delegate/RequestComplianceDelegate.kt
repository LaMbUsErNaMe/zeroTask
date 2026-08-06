package com.example.zero.orchestartor.delegate

import com.example.zero.kafka.CompliancePublisher
import com.example.zero.kafka.dto.ComplianceRequestEvent
import com.example.zero.orchestartor.helper.fail
import com.example.zero.orchestartor.model.ProcessVariables
import org.camunda.bpm.engine.delegate.DelegateExecution
import org.camunda.bpm.engine.delegate.JavaDelegate
import org.springframework.stereotype.Component

@Component("requestComplianceDelegate")
class RequestComplianceDelegate(private val publisher: CompliancePublisher) : JavaDelegate {
    override fun execute(execution: DelegateExecution) {
        runCatching {
            publisher.send(
                ComplianceRequestEvent(
                    login = execution.getVariable(ProcessVariables.LOGIN) as String,
                    inn = execution.getVariable(ProcessVariables.INN) as String,
                    businessKey = execution.processBusinessKey,
                )
            )
        }.getOrElse {
            execution.fail("COMPLIANCE_UNAVAILABLE", it.message ?: "Compliance request failed")
        }
    }
}
