package com.example.zero.aspect

import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.transaction.support.TransactionSynchronization
import org.springframework.transaction.support.TransactionSynchronizationManager

@Aspect
@Component
class TransactionalAspect {

    private val log = LoggerFactory.getLogger(this::class.java)

    @Around("@annotation(org.springframework.transaction.annotation.Transactional)")
    fun measureTransactionTime(pjp: ProceedingJoinPoint): Any? {
        val method = pjp.signature.toShortString()
        val start = System.currentTimeMillis()

        log.info("Transaction [$method] started")

        return try {
            val result = pjp.proceed()

            if (TransactionSynchronizationManager.isActualTransactionActive()) {
                TransactionSynchronizationManager.registerSynchronization(object : TransactionSynchronization {
                    override fun afterCompletion(status: Int) {
                        val duration = System.currentTimeMillis() - start
                        val statusText = when (status) {
                            TransactionSynchronization.STATUS_COMMITTED -> "COMMITTED"
                            TransactionSynchronization.STATUS_ROLLED_BACK -> "ROLLED_BACK"
                            else -> "UNKNOWN"
                        }
                        log.info("Transaction [$method] finished ($statusText) in ${duration}ms")
                    }
                })
            } else {
                val duration = System.currentTimeMillis() - start
                log.info("No active transaction for [$method], took ${duration}ms")
            }

            result
        } catch (ex: Exception) {
            val duration = System.currentTimeMillis() - start
            log.error("Transaction [$method] failed after ${duration}ms", ex)
            throw ex
        }
    }
}
