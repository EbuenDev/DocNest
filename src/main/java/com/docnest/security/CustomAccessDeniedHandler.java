package com.docnest.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Instant;

@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        String json = String.format("{\"timestamp\":\"%s\",\"status\":403,\"error\":\"Forbidden\",\"message\":\"You do not have permission to access this resource.\",\"path\":\"%s\",\"code\":\"AUTH_403\"}",
                Instant.now().toString(), request.getRequestURI());
        response.getWriter().write(json);
    }
} 