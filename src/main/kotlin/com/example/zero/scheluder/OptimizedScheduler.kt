package com.example.zero.scheluder

import com.example.zero.services.ProductService
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
@ConditionalOnProperty(prefix = "app", name = ["schedule.optimization"], havingValue = "true")
class OptimizedScheduler (
    private val productService: ProductService
){
    @Scheduled(fixedDelayString = $$"${app.schedule.period}")
     fun priceUpScheduler() {
        productService.priceUpOpt()
    }
}
