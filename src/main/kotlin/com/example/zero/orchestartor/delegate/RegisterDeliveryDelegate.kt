package com.example.zero.orchestartor.delegate

import com.example.zero.integration.DeliveryClient
import com.example.zero.integration.dto.delivery.DeliveryRegistrationRequest
import com.example.zero.orchestartor.helper.fail
import com.example.zero.orchestartor.helper.orderId
import com.example.zero.orchestartor.model.ProcessVariables
import org.camunda.bpm.engine.delegate.DelegateExecution
import org.camunda.bpm.engine.delegate.JavaDelegate
import org.springframework.stereotype.Component

@Component("registerDeliveryDelegate")
class RegisterDeliveryDelegate(private val client: DeliveryClient) : JavaDelegate {
    override fun execute(execution: DelegateExecution) {
        runCatching {
            client.register(
                DeliveryRegistrationRequest(
                    orderId = execution.orderId(),
                    deliveryAddress = execution.getVariable(ProcessVariables.DELIVERY_ADDRESS) as String,
                )
            )
        }.onSuccess { execution.setVariable(ProcessVariables.DELIVERY_DATE, it.deliveryDate.toString()) }
            .getOrElse { execution.fail("DELIVERY_REGISTRATION_FAILED", it.message ?: "Delivery registration failed") }
    }
}
