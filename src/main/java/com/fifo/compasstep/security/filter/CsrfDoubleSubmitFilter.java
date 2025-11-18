package com.fifo.compasstep.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fifo.compasstep.apipayload.ApiResponse;
import com.fifo.compasstep.security.exceptions.SecurityErrorStatus;
import com.fifo.compasstep.security.util.CookieUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class CsrfDoubleSubmitFilter extends OncePerRequestFilter {
    private final CookieUtil cookieUtil;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private static final String CSRF_HEADER_NAME = "X-CSRF-TOKEN";

    public CsrfDoubleSubmitFilter(CookieUtil cookieUtil) {
        this.cookieUtil = cookieUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        // 로그인, 토큰발급, 로그아웃, Swagger 관련 경로는 제외
        String path = request.getRequestURI();
        if ( // 관리자 쪽 api
                path.startsWith("/api/admin/login") ||
                path.startsWith("/api/auth/refresh") ||
                path.startsWith("/api/admin/logout") ||
                path.startsWith("/api/admin/password/change") ||
                        path.startsWith("/api/user/files/download") ||
                        //user쪽 api
                path.startsWith("/api/user/auth/login") ||
                path.startsWith("/api/user/auth/logout") ||
                        //swagger쪽 api
                path.startsWith("/api-docs") ||
                path.startsWith("/swagger-ui") ||
                path.startsWith("/v3/api-docs") ||
                path.equals("/swagger-ui.html")) {
            filterChain.doFilter(request, response);
            return;
        }
        // GET, OPTIONS 등 안전한 메서드는 제외
        String method = request.getMethod();
        if (method.equalsIgnoreCase("GET") || method.equalsIgnoreCase("OPTIONS") || method.equalsIgnoreCase("HEAD")) {
            filterChain.doFilter(request, response);
            return;
        }
        // 쿠키와 헤더의 CSRF 토큰 비교
        String csrfCookie = cookieUtil.getCsrfTokenFromCookie(request);
        String csrfHeader = request.getHeader(CSRF_HEADER_NAME);
        if (csrfCookie == null || csrfHeader == null || !csrfCookie.equals(csrfHeader)) {
            ApiResponse<Object> apiResponse = ApiResponse.onFailure(
                    SecurityErrorStatus.INVALID_CSRF_TOKEN.getCode(),
                    SecurityErrorStatus.INVALID_CSRF_TOKEN.getMessage(),
                    null
            );

            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write(objectMapper.writeValueAsString(apiResponse));
            return;
        }
        filterChain.doFilter(request, response);
    }
}
