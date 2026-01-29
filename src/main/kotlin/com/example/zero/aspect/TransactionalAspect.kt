package com.example.zero.aspect

import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Aspect
@Component
class TransactionalAspect{

    private val log = LoggerFactory.getLogger(this.javaClass.name)

    @Around("@annotation(org.springframework.transaction.annotation.Transactional)")
    fun measureTransactionTime(pjp: ProceedingJoinPoint): Any? {
        val method = pjp.signature.toShortString()
        val start = System.currentTimeMillis()
        log.info("Transaction [$method] started")

        try {
            return pjp.proceed()
        } finally {
            val durationMs = (System.currentTimeMillis() - start)
            log.info("Transaction [$method] took $durationMs ms")
        }
    }
}