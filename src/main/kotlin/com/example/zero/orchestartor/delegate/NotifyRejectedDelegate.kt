package com.example.zero.orchestartor.delegate

import com.example.zero.integration.NotificationClient
import com.example.zero.integration.dto.notification.NotificationRequest
import com.example.zero.orchestartor.helper.orderId
import com.example.zero.orchestartor.model.ProcessVariables
import org.camunda.bpm.engine.delegate.DelegateExecution
import org.camunda.bpm.engine.delegate.JavaDelegate
import org.springframework.stereotype.Component

@Component("notifyRejectedDelegate")
class NotifyRejectedDelegate(private val client: NotificationClient) : JavaDelegate {
    override fun execute(execution: DelegateExecution) {
        client.send(
            NotificationRequest(
                orderId = execution.orderId(),
                login = execution.getVariable(ProcessVariables.LOGIN) as String,
                message = "Order rejected: ${execution.getVariable(ProcessVariables.REJECTION_REASON) ?: "unknown reason"}",
            )
        )
    }
}
