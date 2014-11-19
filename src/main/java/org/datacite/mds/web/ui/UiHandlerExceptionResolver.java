package org.datacite.mds.web.ui;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.datacite.mds.service.SecurityException;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.DefaultHandlerExceptionResolver;

/**
 * <p>
 * This class is executed in the handlerExceptionResolver chain at first place,
 * to log exceptions for our UI controllers. 
 * </p>
 * 
 * @see DefaultHandlerExceptionResolver
 */
@Component
public class UiHandlerExceptionResolver extends DefaultHandlerExceptionResolver {
    
    Logger log = Logger.getLogger(UiHandlerExceptionResolver.class);

    public UiHandlerExceptionResolver() {
        super();
        setOrder(HIGHEST_PRECEDENCE); // ensure this resolver is fired first
        Class<?>[] handlers = { UiController.class,  };
        setMappedHandlerClasses(handlers); // use our UI controller classes
    }

    @Override
    protected ModelAndView doResolveException(HttpServletRequest request, HttpServletResponse response, Object handler,
            Exception ex) {
        ModelAndView mav = super.doResolveException(request, response, handler, ex);
        if (mav == null) {//not handled well by super method
            if (ex instanceof IllegalArgumentException) { //e.g. missing required request param
            } else if (ex instanceof SecurityException) {
                mav = new ModelAndView();
                mav.setViewName("accessDenied");
                mav.addObject("exception", ex);
            } else {
                log.error("uncaught exception", ex);
            }
        }
        return mav;
    }

}
