package com.example.zero.orchestartor.delegate

import com.example.zero.orchestartor.helper.fail
import com.example.zero.orchestartor.helper.orderId
import com.example.zero.orchestartor.model.ProcessVariables
import com.example.zero.services.OrderService
import org.camunda.bpm.engine.delegate.DelegateExecution
import org.camunda.bpm.engine.delegate.JavaDelegate
import org.springframework.stereotype.Component
import java.time.LocalDate

@Component("updateOrderStatusDelegate")
class UpdateOrderStatusDelegate(private val orderService: OrderService) : JavaDelegate {
    override fun execute(execution: DelegateExecution) {
        runCatching {
            if (execution.getVariable(ProcessVariables.ERROR_FLAG) == true) {
                orderService.cancelConfirmation(
                    execution.orderId(),
                    execution.getVariable(ProcessVariables.REJECTION_REASON) as? String,
                )
            } else {
                orderService.completeConfirmation(
                    execution.orderId(),
                    LocalDate.parse(execution.getVariable(ProcessVariables.DELIVERY_DATE) as String),
                )
            }
        }.getOrElse { execution.fail("ORDER_CALLBACK_FAILED", it.message ?: "Order callback failed") }
    }
}
