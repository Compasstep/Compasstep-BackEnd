package com.fifo.compasstep.security.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fifo.compasstep.apipayload.ApiResponse;
import com.fifo.compasstep.security.exceptions.SecurityErrorStatus;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;


import java.io.IOException;
import java.security.Security;

@RequiredArgsConstructor
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException)
        throws IOException, ServletException {
        ApiResponse<Object> apiResponse = ApiResponse.onFailure(
                SecurityErrorStatus.INVALID_TOKEN.getCode(),
                SecurityErrorStatus.INVALID_TOKEN.getMessage(),
                null);
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.getWriter().write(objectMapper.writeValueAsString(apiResponse));

    }
}
