package com.prithvianilk.mybestofriendo.contextservice.logging;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class WideEventLoggingAspect {

    private final ObjectMapper objectMapper;

    @Around("@annotation(WithWideEventLogging)")
    public Object logWideEvent(ProceedingJoinPoint joinPoint) throws Throwable {
        try {
            return joinPoint.proceed();
        } finally {
            Map<String, Object> context = WideEventContext.getContext();
            if (!context.isEmpty()) {
                try {
                    String jsonContext = objectMapper.writeValueAsString(context);
                    log.info("Wide Event: method={}.{}, context={}",
                            joinPoint.getSignature().getDeclaringTypeName(),
                            joinPoint.getSignature().getName(),
                            jsonContext);
                } catch (Exception e) {
                    log.error("Failed to serialize wide event context to JSON", e);
                    log.info("Wide Event (fallback): method={}.{}, context={}",
                            joinPoint.getSignature().getDeclaringTypeName(),
                            joinPoint.getSignature().getName(),
                            context);
                }
            }
            WideEventContext.clear();
        }
    }
}
