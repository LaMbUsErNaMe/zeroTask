package com.example.zero.aspect

import io.github.oshai.kotlinlogging.KotlinLogging
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.springframework.stereotype.Component

@Aspect
@Component
class MeasureTimeAspect{

    @Around("@annotation(com.example.zero.annotation.MeasureExecTime)")
    fun measureExecTime(pjp: ProceedingJoinPoint): Any?{
        val method = pjp.signature.toShortString()
        val start = System.currentTimeMillis()
        logger.info { "Function [$method] started" }

        try {
            return pjp.proceed()
        } finally {
            val durationMs = (System.currentTimeMillis() - start)
            logger.debug { "Function [$method] took $durationMs ms" }
        }
    }

    private companion object {
        val logger = KotlinLogging.logger {}
    }
}
