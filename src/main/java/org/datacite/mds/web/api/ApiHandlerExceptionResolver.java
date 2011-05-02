package org.datacite.mds.web.api;

import java.io.IOException;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.datacite.mds.service.HandleException;
import org.datacite.mds.service.SecurityException;
import org.datacite.mds.util.ValidationUtils;
import org.datacite.mds.validation.ValidationException;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.DefaultHandlerExceptionResolver;

/**
 * <p>
 * This class is executed in the handlerExceptionResolver chain at first place,
 * to handle exceptions for our API controllers. All exceptions will be handle
 * as http response with correct status codes and body containing a single line
 * </p>
 * 
 * <p>
 * Some methods in DefaultHandlerExceptionResolver only set a status code and no
 * verbose error message, so the response body is empty. These method are
 * overwritten here to send a message as well
 * </p>
 * 
 * @see DefaultHandlerExceptionResolver
 */
@Component
public class ApiHandlerExceptionResolver extends DefaultHandlerExceptionResolver {

    public ApiHandlerExceptionResolver() {
        super();
        setOrder(HIGHEST_PRECEDENCE); // ensure this resolver is fired first
        Class<?>[] handlers = { ApiController.class };
        setMappedHandlerClasses(handlers); // use our API controller classes
    }

    @Override
    protected ModelAndView doResolveException(HttpServletRequest request, HttpServletResponse response, Object handler,
            Exception ex) {
        // call super method with wrapped response to return a reponse body with
        // a single line
        HttpServletResponse wrappedResponse = new ApiResponseWrapper(response);
        ModelAndView mav = super.doResolveException(request, wrappedResponse, handler, ex);

        if (mav != null) // super method handled the exception
            return mav;

        try {
            handleExceptions(ex, wrappedResponse);
        } catch (IOException e) {
            logger.debug(e);
        }
        
        return new ModelAndView();
    }

    private void handleExceptions(Throwable ex, HttpServletResponse response) throws IOException {
        logger.debug(ex);
        if (ex instanceof ValidationException) {
            handleCause(ex,response);
        } else if (ex instanceof ConstraintViolationException) {
            ConstraintViolationException constraintException = (ConstraintViolationException) ex;
            Set<ConstraintViolation<?>> violations = constraintException.getConstraintViolations();
            String msg = ValidationUtils.collateViolationMessages(violations);
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, msg);
        } else if (ex instanceof javax.validation.ValidationException) {
            handleCause(ex, response);
        } else if (ex instanceof SecurityException) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, ex.getMessage());
        } else if (ex instanceof NotFoundException) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, ex.getMessage());
        } else if (ex instanceof HandleException) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, ex.getMessage());
        } else if (ex instanceof DeletedException) {
            response.sendError(HttpServletResponse.SC_GONE, ex.getMessage());
        } else {
            logger.warn("uncaught exception: " + ExceptionUtils.getThrowableList(ex));
            String message = "uncaught exception (" + ex.getMessage() + ")";
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, message);
        }
    }
    
    private void handleCause(Throwable ex, HttpServletResponse response) throws IOException {
        Throwable cause = ex.getCause();
        handleExceptions(cause, response);
    }
    
    @Override
    protected ModelAndView handleMissingServletRequestParameter(MissingServletRequestParameterException ex,
            HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        response.sendError(HttpServletResponse.SC_BAD_REQUEST, ex.getMessage());
        return new ModelAndView();
    }

}
