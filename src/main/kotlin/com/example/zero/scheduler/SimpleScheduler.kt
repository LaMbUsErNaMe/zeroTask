package com.example.zero.scheduler

import com.example.zero.services.ProductService
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
@ConditionalOnProperty(prefix = "app", name = ["schedule.optimization"], havingValue = "false")
class SimpleScheduler (
    private val productService: ProductService
){
    @Scheduled(fixedDelayString = $$"${app.schedule.period}")
    fun priceUpScheduler() {
        productService.priceUp()
    }
}
