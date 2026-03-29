package com.example.zero.aspect

import io.github.oshai.kotlinlogging.KotlinLogging
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.springframework.stereotype.Component
import org.springframework.transaction.support.TransactionSynchronization
import org.springframework.transaction.support.TransactionSynchronizationManager

@Aspect
@Component
class TransactionalAspect {

    @Around("@annotation(org.springframework.transaction.annotation.Transactional)")
    fun measureTransactionTime(pjp: ProceedingJoinPoint): Any? {
        val method = pjp.signature.toShortString()
        val start = System.currentTimeMillis()

        logger.info{"Transaction [$method] started"}

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
                        logger.info{ "Transaction [$method] finished ($statusText) in ${duration}ms" }
                    }
                })
            } else {
                val duration = System.currentTimeMillis() - start
                logger.info { "No active transaction for [$method], took ${duration}ms" }
            }

            result
        } catch (ex: Exception) {
            val duration = System.currentTimeMillis() - start
            logger.error{ "Transaction [$method] failed after ${duration}ms $ex" }
            throw ex
        }
    }

    private companion object {
        val logger = KotlinLogging.logger {}
    }
}
