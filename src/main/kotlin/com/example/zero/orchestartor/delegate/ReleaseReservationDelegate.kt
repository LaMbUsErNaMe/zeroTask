package com.example.zero.orchestrator.delegate

import com.example.zero.orchestartor.helper.orderId
import com.example.zero.services.OrderService
import org.camunda.bpm.engine.delegate.DelegateExecution
import org.camunda.bpm.engine.delegate.JavaDelegate
import org.springframework.stereotype.Component

@Component("releaseReservationDelegate")
class ReleaseReservationDelegate(
    private val orderService: OrderService,
) : JavaDelegate {
    override fun execute(execution: DelegateExecution) {
        val orderId = execution.orderId()
        orderService.releaseReservation(orderId)
    }
}
