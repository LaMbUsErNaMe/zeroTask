package com.example.zero.orchestartor

import com.example.zero.controller.dto.process.ContinueOrderProcessRequest
import com.example.zero.orchestartor.model.ProcessMessages
import com.example.zero.orchestartor.model.ProcessVariables
import com.example.zero.services.OrderService
import org.camunda.bpm.engine.RuntimeService
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class OrderConfirmationProcessService(
    private val runtimeService: RuntimeService,
    private val orderService: OrderService,
) {
    fun start(customerId: Long, orderId: UUID): String {
        orderService.checkOrderOwner(customerId, orderId)

        val runningInstance = runtimeService.createProcessInstanceQuery()
            .processDefinitionKey(PROCESS_KEY)
            .processInstanceBusinessKey(orderId.toString())
            .active()
            .list()
            .firstOrNull()

        if (runningInstance != null) {
            return runningInstance.processInstanceId
        }

        check(orderService.canStartConfirmation(customerId, orderId)) {
            "Заказ [$orderId] нельзя подтвердить в текущем состоянии - статус отличный от CREATED"
        }

        return runtimeService
            .createProcessInstanceByKey(PROCESS_KEY)
            .businessKey(orderId.toString())
            .setVariable(ProcessVariables.ORDER_ID, orderId.toString())
            .setVariable(ProcessVariables.CUSTOMER_ID, customerId)
            .execute()
            .processInstanceId
    }

    fun continueProcess(orderId: UUID, request: ContinueOrderProcessRequest) {
        runtimeService
            .createMessageCorrelation(ProcessMessages.ORDER_CONFIRMATION_RESULT)
            .processInstanceBusinessKey(orderId.toString())
            .setVariable(ProcessVariables.CONFIRMATION_SUCCESS, request.success)
            .setVariable(ProcessVariables.REJECTION_REASON, request.reason)
            .correlate()
    }

    companion object {
        const val PROCESS_KEY = "order-confirmation"
    }
}
