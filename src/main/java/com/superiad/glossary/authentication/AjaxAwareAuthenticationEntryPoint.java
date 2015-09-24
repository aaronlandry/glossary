package com.superiad.glossary.authentication;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint; 
import javax.servlet.ServletException;
import javax.servlet.http.*;
import java.io.IOException;

/**
 * Gracefully handle login timeouts with AJAX calls.
 * @author Aaron
 */
public class AjaxAwareAuthenticationEntryPoint extends LoginUrlAuthenticationEntryPoint {
 
    public AjaxAwareAuthenticationEntryPoint(String loginUrl) {
        super(loginUrl);
    }
 
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
            AuthenticationException authException) throws IOException, ServletException {
        boolean isAjax = request.getHeader("Accept").contains("application/json");
        if (isAjax) {
            response.sendError(403, "Forbidden");
        } 
        else {
            super.commence(request, response, authException);
        }
    }
}