package com.fifo.compasstep.security.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fifo.compasstep.admin.exceptions.AdminErrorStatus;
import com.fifo.compasstep.apipayload.ApiResponse;
import com.fifo.compasstep.apipayload.code.BaseErrorCode;
import com.fifo.compasstep.security.exceptions.SecurityErrorStatus;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class CustomAccessDenyHandler implements AccessDeniedHandler {

    private final ObjectMapper objectMapper;

    @Override
    public void handle(HttpServletRequest request,
                       HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException, ServletException {

        BaseErrorCode errorCode = AdminErrorStatus.ACCESS_DENIED; // 403 에러 코드 사용

        ApiResponse<Object> apiResponse = ApiResponse.onFailure(
                errorCode.getReason().getCode(),
                errorCode.getReason().getMessage(),
                null
        );

        response.setStatus(errorCode.getReasonHttpStatus().getHttpStatus().value()); // 403 상태 코드
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(objectMapper.writeValueAsString(apiResponse));
    }
}