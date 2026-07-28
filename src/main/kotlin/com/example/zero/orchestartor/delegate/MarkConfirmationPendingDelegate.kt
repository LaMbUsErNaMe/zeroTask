package com.example.zero.orchestrator.delegate

import com.example.zero.orchestartor.model.ProcessVariables
import com.example.zero.services.OrderService
import org.camunda.bpm.engine.delegate.DelegateExecution
import org.camunda.bpm.engine.delegate.JavaDelegate
import org.springframework.stereotype.Component
import java.util.UUID

@Component("markConfirmationPendingDelegate")
class MarkConfirmationPendingDelegate(
    private val orderService: OrderService,
) : JavaDelegate {
    override fun execute(execution: DelegateExecution) {
        val customerId = execution.getVariable(ProcessVariables.CUSTOMER_ID) as Long
        val orderId = UUID.fromString(execution.getVariable(ProcessVariables.ORDER_ID) as String)
        orderService.markConfirmationPending(customerId, orderId)
    }
}
