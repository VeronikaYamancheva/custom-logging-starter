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
public class RepositoryLoggingAspect extends AbstractLoggingAspect {

    @Pointcut("execution(* *..*Repository.*(..))")
    public void repositoryMethod() {
    }

    @Around(value = "repositoryMethod()")
    public Object logRepositoryMethod(ProceedingJoinPoint joinPoint) {
        Class<?> repositoryClass = joinPoint.getTarget().getClass();
        Object[] joinPointArgs = joinPoint.getArgs();
        String request = getRequestStringFromArgs(joinPointArgs);

        log.info("Repository {} start with request: {}", repositoryClass.getSimpleName(), request);
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
        log.info("Response {} end with response: {}", repositoryClass.getSimpleName(), response);
        return result;
    }

    @AfterThrowing(pointcut = "repositoryMethod()", throwing = "e")
    public void logAfterThrowing(JoinPoint joinPoint, Throwable e) {
        Class<?> repositoryClass = joinPoint.getTarget().getClass();
        log.error("Error in repository {}. Error message: {}", repositoryClass.getSimpleName(), e.getMessage(), e);
    }
}

