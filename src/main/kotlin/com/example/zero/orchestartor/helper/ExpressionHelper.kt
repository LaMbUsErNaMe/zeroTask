package com.example.zero.orchestartor.helper

import com.example.zero.orchestartor.model.ProcessVariables
import org.camunda.bpm.engine.delegate.DelegateExecution
import org.springframework.stereotype.Component

@Component("expressionHelper")
class ExpressionHelper {
    fun orderValid(execution: DelegateExecution): Boolean =
        execution.getVariable(ProcessVariables.ORDER_VALID) == true

    fun confirmationSuccessful(execution: DelegateExecution): Boolean =
        execution.getVariable(ProcessVariables.CONFIRMATION_SUCCESS) == true
}
