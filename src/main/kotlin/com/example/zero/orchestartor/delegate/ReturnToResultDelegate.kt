package com.example.zero.orchestartor.delegate

import org.camunda.bpm.engine.RuntimeService
import org.camunda.bpm.engine.delegate.DelegateExecution
import org.camunda.bpm.engine.delegate.JavaDelegate
import org.camunda.bpm.engine.impl.cfg.TransactionState
import org.camunda.bpm.engine.impl.context.Context
import org.springframework.stereotype.Component

@Component("returnToResultDelegate")
class ReturnToResultDelegate(
    private val runtimeService: RuntimeService,
) : JavaDelegate {
    override fun execute(execution: DelegateExecution) {
        val processInstanceId = execution.processInstanceId
        Context.getCommandContext().transactionContext.addTransactionListener(TransactionState.COMMITTED) {
            runtimeService.createProcessInstanceModification(processInstanceId)
                .cancelAllForActivity(WAIT_FOR_RETURN_ACTIVITY_ID)
                .startAfterActivity(RETURN_AFTER_ACTIVITY_ID)
                .execute()
        }
    }

    companion object {
        const val RETURN_AFTER_ACTIVITY_ID = "TaskPayOrder"
        const val WAIT_FOR_RETURN_ACTIVITY_ID = "EndEventErrorHandled"
    }
}
