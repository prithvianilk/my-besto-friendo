package com.prithvianilk.mybestofriendo.contextservice.logging;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Aspect that handles wide event logging for methods annotated
 * with @WithWideEventLogging.
 * Logs the accumulated context at the end of method execution and clears the
 * ThreadLocal.
 */
@Slf4j
@Aspect
@Component
public class WideEventLoggingAspect {

    @Around("@annotation(WithWideEventLogging)")
    public Object logWideEvent(ProceedingJoinPoint joinPoint) throws Throwable {
        try {
            return joinPoint.proceed();
        } finally {
            Map<String, Object> context = WideEventContext.getContext();
            if (!context.isEmpty()) {
                log.info("Wide Event: method={}.{}, context={}",
                        joinPoint.getSignature().getDeclaringTypeName(),
                        joinPoint.getSignature().getName(),
                        context);
            }
            WideEventContext.clear();
        }
    }
}
