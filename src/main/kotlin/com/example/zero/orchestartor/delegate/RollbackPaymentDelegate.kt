package com.example.zero.orchestartor.delegate

import com.example.zero.integration.PaymentClient
import com.example.zero.orchestartor.helper.orderId
import org.camunda.bpm.engine.delegate.DelegateExecution
import org.camunda.bpm.engine.delegate.JavaDelegate
import org.springframework.stereotype.Component

@Component("rollbackPaymentDelegate")
class RollbackPaymentDelegate(private val client: PaymentClient) : JavaDelegate {
    override fun execute(execution: DelegateExecution) = client.rollback(execution.orderId())
}
