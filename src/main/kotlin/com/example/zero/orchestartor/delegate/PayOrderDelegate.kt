package com.example.zero.orchestartor.delegate

import com.example.zero.integration.PaymentClient
import com.example.zero.integration.dto.payment.PaymentRequest
import com.example.zero.orchestartor.helper.fail
import com.example.zero.orchestartor.helper.orderId
import com.example.zero.orchestartor.model.ProcessVariables
import org.camunda.bpm.engine.delegate.DelegateExecution
import org.camunda.bpm.engine.delegate.JavaDelegate
import org.springframework.stereotype.Component
import java.math.BigDecimal

@Component("payOrderDelegate")
class PayOrderDelegate(private val client: PaymentClient) : JavaDelegate {
    override fun execute(execution: DelegateExecution) {
        val result = runCatching {
            client.pay(
                PaymentRequest(
                    orderId = execution.orderId(),
                    amount = execution.getVariable(ProcessVariables.ORDER_AMOUNT) as BigDecimal,
                    accountNumber = execution.getVariable(ProcessVariables.ACCOUNT_NUMBER) as String,
                )
            )
        }.getOrElse { execution.fail("PAYMENT_FAILED", it.message ?: "Payment service failed") }

        if (!result.success) execution.fail("PAYMENT_REJECTED", result.reason ?: "Payment was rejected")
    }
}
