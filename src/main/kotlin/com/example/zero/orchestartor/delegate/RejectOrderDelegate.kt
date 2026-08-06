package com.example.zero.orchestartor.delegate

import com.example.zero.orchestartor.helper.orderId
import com.example.zero.orchestartor.model.ProcessVariables
import com.example.zero.services.OrderService
import org.camunda.bpm.engine.delegate.DelegateExecution
import org.camunda.bpm.engine.delegate.JavaDelegate
import org.springframework.stereotype.Component

@Component("rejectOrderDelegate")
class RejectOrderDelegate(
    private val orderService: OrderService,
) : JavaDelegate {
    override fun execute(execution: DelegateExecution) {
        val orderId = execution.orderId()
        val reason = execution.getVariable(ProcessVariables.REJECTION_REASON) as? String
        orderService.rejectConfirmation(orderId, reason)
    }
}
