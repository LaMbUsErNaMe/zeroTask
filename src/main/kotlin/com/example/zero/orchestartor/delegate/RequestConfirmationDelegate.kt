package com.example.zero.orchestartor.delegate

import com.example.zero.orchestartor.model.OrderConfirmationRequest
import com.example.zero.orchestartor.model.ProcessVariables
import org.camunda.bpm.engine.delegate.DelegateExecution
import org.camunda.bpm.engine.delegate.JavaDelegate
import org.camunda.bpm.engine.variable.Variables
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component("requestConfirmationDelegate")
class RequestConfirmationDelegate : JavaDelegate {
    override fun execute(execution: DelegateExecution) {
        val request = OrderConfirmationRequest(
            orderId = execution.getVariable(ProcessVariables.ORDER_ID) as String,
            customerId = execution.getVariable(ProcessVariables.CUSTOMER_ID) as Long,
            requestedAt = LocalDateTime.now(),
        )

        val serializedRequest = Variables.objectValue(request)
            .serializationDataFormat(Variables.SerializationDataFormats.JSON)
            .create()

        execution.setVariable(ProcessVariables.CONFIRMATION_REQUEST, serializedRequest)
    }
}
