package org.datacite.mds.aspects;

import org.apache.log4j.Logger;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.datacite.mds.util.SecurityUtils;
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
        Logger log = getLogger(joinPoint);
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
    
    @Pointcut("within(org.datacite.mds.domain.*)")
    public void withinDomain() {
    }
    
    @Pointcut("execution(public * *.persist()) || execution(public * *.merge()) || execution(public * *.remove())")
    public void crud() {
    }

    @AfterReturning("withinDomain() && crud()")
    public void logCrud(JoinPoint joinPoint) {
        Logger log = getLogger(joinPoint);
        String method = joinPoint.getSignature().getName();
        Object target = joinPoint.getTarget();
        log.debug(method + "(): " + target);
    }
    
    @AfterReturning("execution(public void org.datacite.mds.domain.Media.remove())")
    public void logDeleteMediaType(JoinPoint joinPoint) {
        Logger log = getLogger(joinPoint);
        Object target = joinPoint.getTarget();
        log.info("deleted " + target);
    }
    
    @AfterReturning("execution(public * org.datacite.mds.domain.AllocatorOrDatacentre.persist())")
    public void logCreateAllocatorOrDatacentre(JoinPoint joinPoint) {
        Logger log = getLogger(joinPoint);
        Object target = joinPoint.getTarget();
        log.info("created " + target);
    }
    
    @Before("execution(@org.springframework.web.bind.annotation.RequestMapping * *.*(..))")
    public void logController(JoinPoint joinPoint) {
        Logger log = getLogger(joinPoint);
        String method = joinPoint.getSignature().getName();
        String symbol = SecurityUtils.getCurrentSymbolOrNull();
        log.debug(method + "() executed by " + symbol);
    }
    
    private Logger getLogger(JoinPoint joinPoint) {
        return Logger.getLogger(joinPoint.getSignature().getDeclaringType());
    }

}