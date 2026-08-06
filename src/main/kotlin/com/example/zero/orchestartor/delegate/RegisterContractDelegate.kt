package com.example.zero.orchestartor.delegate

import com.example.zero.integration.ContractClient
import com.example.zero.integration.dto.contract.ContractRegistrationRequest
import com.example.zero.orchestartor.helper.fail
import com.example.zero.orchestartor.model.ProcessVariables
import org.camunda.bpm.engine.delegate.DelegateExecution
import org.camunda.bpm.engine.delegate.JavaDelegate
import org.springframework.stereotype.Component

@Component("registerContractDelegate")
class RegisterContractDelegate(private val client: ContractClient) : JavaDelegate {
    override fun execute(execution: DelegateExecution) {
        runCatching {
            client.register(
                ContractRegistrationRequest(
                    inn = execution.getVariable(ProcessVariables.INN) as String,
                    accountNumber = execution.getVariable(ProcessVariables.ACCOUNT_NUMBER) as String,
                )
            )
        }.onSuccess { execution.setVariable(ProcessVariables.CONTRACT_ID, it.contractId) }
            .getOrElse { execution.fail("CONTRACT_REGISTRATION_FAILED", it.message ?: "Contract registration failed") }
    }
}
