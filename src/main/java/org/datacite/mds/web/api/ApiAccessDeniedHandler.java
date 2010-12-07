package org.datacite.mds.web.api;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandlerImpl;

/**
 * This class is a simple wrapper for AccessDeniedHandlerImpl to use our
 * ApiResponseWrapper and therefore put the exception message sole into the
 * response body
 * 
 * @see ApiResponseWrapper
 */
public class ApiAccessDeniedHandler extends AccessDeniedHandlerImpl {

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
            AccessDeniedException accessDeniedException) throws IOException, ServletException {
        HttpServletResponse wrappedResponse = new ApiResponseWrapper(response);
        super.handle(request, wrappedResponse, accessDeniedException);
    }

}
