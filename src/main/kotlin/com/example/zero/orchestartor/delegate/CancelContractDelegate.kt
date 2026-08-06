package com.example.zero.orchestartor.delegate

import com.example.zero.integration.ContractClient
import com.example.zero.orchestartor.model.ProcessVariables
import org.camunda.bpm.engine.delegate.DelegateExecution
import org.camunda.bpm.engine.delegate.JavaDelegate
import org.springframework.stereotype.Component

@Component("cancelContractDelegate")
class CancelContractDelegate(private val client: ContractClient) : JavaDelegate {
    override fun execute(execution: DelegateExecution) {
        (execution.getVariable(ProcessVariables.CONTRACT_ID) as? String)?.let(client::cancel)
    }
}
