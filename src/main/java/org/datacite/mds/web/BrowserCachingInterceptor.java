package org.datacite.mds.web;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.mvc.WebContentInterceptor;

public class BrowserCachingInterceptor extends WebContentInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws ServletException {
        // fix to stop tomcat from adding "Pragma: No-Cache" for CONFIDENTIAL resources (HTTPS)
        response.setHeader("Pragma", "");
        return super.preHandle(request, response, handler);
    }

}
