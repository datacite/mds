package org.datacite.mds.aspects;

import org.apache.log4j.Logger;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.datacite.mds.util.ValidationUtils;
import org.springframework.validation.BindingResult;

@Aspect
public class LoggingAspect {

    @Pointcut("within(org.datacite.mds.web.ui.controller..*)")
    public void anyUiController() {
    }

    @Pointcut("execution(public * *(.., org.springframework.validation.BindingResult, ..))")
    public void hasBindingResultParam() {
    }

    @AfterReturning(pointcut = "anyUiController() && hasBindingResultParam()")
    public void logUiFormErrors(JoinPoint joinPoint) {
        Logger log = Logger.getLogger(joinPoint.getSignature().getDeclaringType());
        String method = joinPoint.getSignature().getName();
        BindingResult result = findFirstArgWithClass(joinPoint, BindingResult.class);
        String errors = ValidationUtils.collateBindingResultErrors(result);
        log.debug("'" + method + "' form errors: " + errors);
    }

    @SuppressWarnings("unchecked")
    private <T> T findFirstArgWithClass(JoinPoint joinPoint, Class<T> clazz) {
        for (Object arg : joinPoint.getArgs())
            if (clazz.isInstance(arg))
                return (T) arg;
        return null;
    }

}