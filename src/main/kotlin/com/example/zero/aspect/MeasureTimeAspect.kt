package com.example.zero.aspect

import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Aspect
@Component
class MeasureTimeAspect{

    private val log = LoggerFactory.getLogger(MeasureTimeAspect::class.java)

    @Around("@annotation(com.example.zero.annotation.MeasureExecTime)")
    fun measureExecTime(pjp: ProceedingJoinPoint): Any?{
        val method = pjp.signature.toShortString()
        val start = System.currentTimeMillis()
        log.info("Function [$method] started")

        try {
            return pjp.proceed()
        } finally {
            val durationMs = (System.currentTimeMillis() - start)
            log.info("Function [$method] took $durationMs ms")
        }
    }
}