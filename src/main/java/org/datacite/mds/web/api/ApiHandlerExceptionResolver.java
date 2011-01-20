package org.datacite.mds.web.api;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
        Class[] handlers = { ApiController.class };
        setMappedHandlerClasses(handlers); // use our API controller classes
    }

    @Override
    protected ModelAndView doResolveException(HttpServletRequest request, HttpServletResponse response, Object handler,
            Exception ex) {
        logger.debug(ex);

        // call super method with wrapped response to return a reponse body with
        // a single line
        HttpServletResponse wrappedResponse = new ApiResponseWrapper(response);
        ModelAndView mav = super.doResolveException(request, wrappedResponse, handler, ex);

        // super method returns null, if it cannot handle the exception
        if (mav == null) {
            try {
                String causes = ex.getMessage();
                Throwable t = ex;
                while (t.getCause() != null) {
                    causes += " --> " + t.getCause().getMessage();
                    t = t.getCause();
                }                
                logger.debug(causes);
                
                // response with INTERNAL SERVER ERROR
                wrappedResponse.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "uncaught exception"); 
                mav = new ModelAndView();
            } catch (IOException e) {
                logger.debug(e);
            }
        }
        return mav;
    }

    @Override
    protected ModelAndView handleMissingServletRequestParameter(MissingServletRequestParameterException ex,
            HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        response.sendError(HttpServletResponse.SC_BAD_REQUEST, ex.getMessage());
        return new ModelAndView();
    }

}
