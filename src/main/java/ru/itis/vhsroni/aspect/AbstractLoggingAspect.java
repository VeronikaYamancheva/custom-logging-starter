package ru.itis.vhsroni.aspect;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

public class AbstractLoggingAspect {

    protected String getRequestStringFromArgs(Object[] args) {
        return (args.length == 0)
                ? "void request"
                : Arrays.stream(args)
                .map(Object::toString)
                .collect(Collectors.joining(", \n"));
    }

    protected String getResponseStringFromResult(Object result) {
        return (result == null)
                ? "null response"
                : (result instanceof Collection<?>)
                ? ((Collection<?>) result).stream()
                .map(Object::toString)
                .collect(Collectors.joining(", \n"))
                : result.toString();
    }
}
