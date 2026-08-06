package com.example.zero.orchestartor.helper

import com.example.zero.orchestartor.model.ProcessVariables
import org.camunda.bpm.engine.delegate.BpmnError
import org.camunda.bpm.engine.delegate.DelegateExecution
import java.util.UUID

private const val INTEGRATION_ERROR = "ORDER_CONFIRMATION_ERROR"

internal fun DelegateExecution.fail(code: String, message: String): Nothing {
    setVariable(ProcessVariables.ERROR_CODE, code)
    setVariable(ProcessVariables.REJECTION_REASON, message)
    throw BpmnError(INTEGRATION_ERROR, message)
}

internal fun DelegateExecution.orderId(): UUID =
    UUID.fromString(getVariable(ProcessVariables.ORDER_ID) as String)
