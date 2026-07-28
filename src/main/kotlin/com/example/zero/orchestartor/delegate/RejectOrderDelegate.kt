package com.example.zero.orchestartor.delegate

import com.example.zero.orchestartor.model.ProcessVariables
import com.example.zero.services.OrderService
import org.camunda.bpm.engine.delegate.DelegateExecution
import org.camunda.bpm.engine.delegate.JavaDelegate
import org.springframework.stereotype.Component
import java.util.UUID

@Component("rejectOrderDelegate")
class RejectOrderDelegate(
    private val orderService: OrderService,
) : JavaDelegate {
    override fun execute(execution: DelegateExecution) {
        val orderId = UUID.fromString(execution.getVariable(ProcessVariables.ORDER_ID) as String)
        val reason = execution.getVariable(ProcessVariables.REJECTION_REASON) as? String
        orderService.rejectConfirmation(orderId, reason)
    }
}
