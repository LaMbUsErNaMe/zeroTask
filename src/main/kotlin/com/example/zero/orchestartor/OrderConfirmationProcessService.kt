package com.example.zero.orchestartor

import com.example.zero.controller.dto.process.ComplianceDecisionRequest
import com.example.zero.orchestartor.model.ProcessMessages
import com.example.zero.orchestartor.model.ProcessVariables
import com.example.zero.services.OrderService
import org.camunda.bpm.engine.RuntimeService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class OrderConfirmationProcessService(
    private val runtimeService: RuntimeService,
    private val orderService: OrderService,
) {
    @Transactional
    fun start(customerId: Long, orderId: UUID): String {
        val context = orderService.getConfirmationContext(customerId, orderId)

        context.existingBusinessKey?.let { existingKey ->
            val runningInstance = runtimeService.createProcessInstanceQuery()
                .processDefinitionKey(PROCESS_KEY)
                .processInstanceBusinessKey(existingKey)
                .active()
                .list()
                .firstOrNull()

            if (runningInstance != null) return existingKey
        }

        check(orderService.canStartConfirmation(customerId, orderId)) {
            "Заказ [$orderId] нельзя подтвердить в текущем состоянии - статус отличный от CREATED"
        }

        val businessKey = UUID.randomUUID().toString()
        orderService.startProcessing(customerId, orderId, businessKey)

        runtimeService
            .createProcessInstanceByKey(PROCESS_KEY)
            .businessKey(businessKey)
            .setVariable(ProcessVariables.ORDER_ID, orderId.toString())
            .setVariable(ProcessVariables.CUSTOMER_ID, customerId)
            .setVariable(ProcessVariables.BUSINESS_KEY, businessKey)
            .setVariable(ProcessVariables.DELIVERY_ADDRESS, context.deliveryAddress)
            .setVariable(ProcessVariables.INN, context.inn)
            .setVariable(ProcessVariables.ACCOUNT_NUMBER, context.accountNumber)
            .setVariable(ProcessVariables.ORDER_AMOUNT, context.amount)
            .setVariable(ProcessVariables.LOGIN, context.login)
            .setVariable(ProcessVariables.ERROR_FLAG, false)
            .execute()

        return businessKey
    }

    fun completeCompliance(orderId: UUID, request: ComplianceDecisionRequest) {
        val processInstance = runtimeService.createProcessInstanceQuery()
            .processDefinitionKey(PROCESS_KEY)
            .variableValueEquals(ProcessVariables.ORDER_ID, orderId.toString())
            .active()
            .singleResult()
            ?: error("Active confirmation process for order [$orderId] was not found")

        runtimeService
            .createMessageCorrelation(ProcessMessages.COMPLIANCE_RESULT)
            .processInstanceId(processInstance.processInstanceId)
            .setVariable(ProcessVariables.COMPLIANCE_SUCCESS, request.approved)
            .setVariable(ProcessVariables.REJECTION_REASON, request.reason)
            .correlate()
    }

    companion object {
        const val PROCESS_KEY = "OrderConfirmation"
    }
}
