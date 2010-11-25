package org.datacite.mds.web.api;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.DefaultHandlerExceptionResolver;

@Component
public class ApiHandlerExceptionResolver extends DefaultHandlerExceptionResolver {
    
    public ApiHandlerExceptionResolver() {
        super();
        setOrder(HIGHEST_PRECEDENCE);
        Class[] handlers = { ApiController.class };
        setMappedHandlerClasses(handlers);
    }

    @Override
    protected ModelAndView doResolveException(HttpServletRequest request, HttpServletResponse response, Object handler,
            Exception ex) {
        logger.debug(ex);
        HttpServletResponse wrappedResponse = new MyResponseWrapper(response);
        ModelAndView mav = super.doResolveException(request, wrappedResponse, handler, ex);
        if (mav == null) {
            try {
                wrappedResponse.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "uncatched exception");
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

class MyResponseWrapper extends HttpServletResponseWrapper {

    Logger log = Logger.getLogger(MyResponseWrapper.class);
    
    public MyResponseWrapper(HttpServletResponse response) {
        super(response);
    }

    @Override
    public void sendError(int sc, String msg) throws IOException {
        log.debug("sendError(sc,msg)");
        setStatus(sc);
        getWriter().println(msg);
    }

    @Override
    public void sendError(int sc) throws IOException {
        log.debug("sendError(sc)");
        sendError(sc,"");
    }
    
}

