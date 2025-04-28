package ru.itis.vhsroni.aspect;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.MDC;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import java.util.UUID;

@Slf4j
@Aspect
public class ControllerLoggingAspect extends AbstractLoggingAspect {

    @Pointcut("execution(* *..*Controller.*(..))")
    public void controllerMethod() {
    }

    @Around(value = "controllerMethod()")
    public Object logControllerMethod(ProceedingJoinPoint joinPoint) {
        putMDCAttributes();

        Class<?> controllerClass = joinPoint.getTarget().getClass();
        Object[] joinPointArgs = joinPoint.getArgs();
        String request = getRequestStringFromArgs(joinPointArgs);
        log.info("Controller {} start with request: {}", controllerClass.getSimpleName(), request);

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
        log.info("Controller {} end with response: {}", controllerClass.getSimpleName(), response);
        return result;
    }

    private void putMDCAttributes() {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (requestAttributes == null) {
            log.warn("RequestAttributes is null. Unable to put MDC attributes.");
            return;
        }
        HttpServletRequest request = (HttpServletRequest)
                requestAttributes.resolveReference(RequestAttributes.REFERENCE_REQUEST);
        if (request == null) {
            log.warn("HttpServletRequest is null. Unable to put MDC attributes.");
            return;
        }
    }

    @AfterThrowing(pointcut = "controllerMethod()", throwing = "e")
    public void logAfterThrowing(JoinPoint joinPoint, Throwable e) {
        Class<?> controllerClass = joinPoint.getTarget().getClass();
        log.error("Error in controller {}. Error message: {}", controllerClass.getSimpleName(), e.getMessage(), e);
    }
}
