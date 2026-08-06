package com.example.zero.orchestartor.delegate

import com.example.zero.integration.DeliveryClient
import com.example.zero.orchestartor.helper.orderId
import org.camunda.bpm.engine.delegate.DelegateExecution
import org.camunda.bpm.engine.delegate.JavaDelegate
import org.springframework.stereotype.Component

@Component("cancelDeliveryDelegate")
class CancelDeliveryDelegate(private val client: DeliveryClient) : JavaDelegate {
    override fun execute(execution: DelegateExecution) = client.cancel(execution.orderId())
}
