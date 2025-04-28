package ru.itis.vhsroni.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.MDC;

@Slf4j
@Aspect
public class ServiceLoggingAspect extends AbstractLoggingAspect{


    @Pointcut("execution(* *..*Service.*(..))")
    public void serviceMethod() {
    }

    @Around(value = "serviceMethod()")
    public Object logServiceMethod(ProceedingJoinPoint joinPoint) {
        Class<?> serviceClass = joinPoint.getTarget().getClass();
        Object[] joinPointArgs = joinPoint.getArgs();
        String request = getRequestStringFromArgs(joinPointArgs);
        log.debug("Service {} start with request: {}", serviceClass.getSimpleName(), request);

        Object result;
        long startExecutionTime = System.currentTimeMillis();
        try {
            result = joinPoint.proceed();
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
        long endExecutionTime = System.currentTimeMillis();

        String executionTime = "%s ms".formatted(endExecutionTime - startExecutionTime);
        MDC.put("executionTime", executionTime);
        String response = getResponseStringFromResult(result);
        log.debug("Service {} end with response: {}", serviceClass.getSimpleName(), response);
        return result;
    }

    @AfterThrowing(pointcut = "serviceMethod()", throwing = "e")
    public void logAfterThrowing(JoinPoint joinPoint, Throwable e) {
        Class<?> serviceClass = joinPoint.getTarget().getClass();
        log.error("Error in service {}. Error message: {}", serviceClass.getSimpleName(), e.getMessage(), e);
    }
}

