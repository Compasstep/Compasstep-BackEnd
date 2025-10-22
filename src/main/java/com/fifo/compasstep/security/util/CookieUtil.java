package com.fifo.compasstep.security.util;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.util.Base64;

@Component
public class CookieUtil {
    private static final String REFRESH_TOKEN_COOKIE_NAME = "refresh_token";
    private static final String ACCESS_TOKEN_COOKIE_NAME = "access_token";
    private static final String CSRF_TOKEN_COOKIE_NAME = "csrf_token";

    @Value("${jwt.refresh-token.expiration:28800}")
    private int REFRESH_TOKEN_COOKIE_MAX_AGE; // 8시간

    @Value("${jwt.access-token.expiration:3600}")
    private int ACCESS_TOKEN_COOKIE_MAX_AGE; // 1시간

    @Value("${cookie.setsecure:}")
    private boolean cookieSetsecure;

    @Value("${cookie.domain:}")
    private String cookieDomain;

    @Value("${cookie.samesite:None}")
    private String cookieSameSite;

    // Access Token 쿠키 생성
    public void createAccessTokenCookie(HttpServletResponse response, String accessToken) {
        ResponseCookie.ResponseCookieBuilder builder = ResponseCookie.from(ACCESS_TOKEN_COOKIE_NAME, accessToken)
                .httpOnly(true)
                .secure(cookieSetsecure)
                .path("/")
                .maxAge(ACCESS_TOKEN_COOKIE_MAX_AGE)
                .sameSite(cookieSameSite);

        if (cookieDomain != null && !cookieDomain.isEmpty()) {
            builder.domain(cookieDomain);
        }

        ResponseCookie cookie = builder.build();
        response.addHeader("Set-Cookie", cookie.toString());
    }

    // Access Token 쿠키 삭제
    public void deleteAccessTokenCookie(HttpServletResponse response) {
        ResponseCookie.ResponseCookieBuilder builder = ResponseCookie.from(ACCESS_TOKEN_COOKIE_NAME, "")
                .httpOnly(true)
                .secure(cookieSetsecure)
                .path("/")
                .maxAge(0)
                .sameSite(cookieSameSite);

        if (cookieDomain != null && !cookieDomain.isEmpty()) {
            builder.domain(cookieDomain);
        }

        ResponseCookie cookie = builder.build();
        response.addHeader("Set-Cookie", cookie.toString());
    }

    // Access Token 쿠키에서 추출
    public String getAccessTokenFromCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (ACCESS_TOKEN_COOKIE_NAME.equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    // CSRF 토큰 쿠키 생성 (HttpOnly=false)
    public String createCsrfTokenCookie(HttpServletResponse response) {
        String csrfToken = generateCsrfToken();

        ResponseCookie.ResponseCookieBuilder builder = ResponseCookie.from(CSRF_TOKEN_COOKIE_NAME, csrfToken)
                .httpOnly(false) // JS에서 읽을 수 있어야 함
                .secure(cookieSetsecure)
                .path("/")
                .maxAge(ACCESS_TOKEN_COOKIE_MAX_AGE) // access token과 동일하게 관리
                .sameSite(cookieSameSite);

        if (cookieDomain != null && !cookieDomain.isEmpty()) {
            builder.domain(cookieDomain);
        }

        ResponseCookie cookie = builder.build();
        response.addHeader("Set-Cookie", cookie.toString());
        return csrfToken;
    }

    // CSRF 토큰 쿠키 삭제
    public void deleteCsrfTokenCookie(HttpServletResponse response) {
        ResponseCookie.ResponseCookieBuilder builder = ResponseCookie.from(CSRF_TOKEN_COOKIE_NAME, "")
                .httpOnly(false)
                .secure(cookieSetsecure)
                .path("/")
                .maxAge(0)
                .sameSite(cookieSameSite);

        if (cookieDomain != null && !cookieDomain.isEmpty()) {
            builder.domain(cookieDomain);
        }

        ResponseCookie cookie = builder.build();
        response.addHeader("Set-Cookie", cookie.toString());
    }

    // CSRF 토큰 쿠키에서 추출
    public String getCsrfTokenFromCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (CSRF_TOKEN_COOKIE_NAME.equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    // 랜덤 CSRF 토큰 생성
    private String generateCsrfToken() {
        byte[] bytes = new byte[32];
        new SecureRandom().nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    // Refresh Token 쿠키 생성
    public void createRefreshTokenCookie(HttpServletResponse response, String tokenId) {
        ResponseCookie.ResponseCookieBuilder builder = ResponseCookie.from(REFRESH_TOKEN_COOKIE_NAME, tokenId)
                .httpOnly(true)
                .secure(cookieSetsecure)
                .path("/")
                .maxAge(REFRESH_TOKEN_COOKIE_MAX_AGE)
                .sameSite(cookieSameSite);

        if (cookieDomain != null && !cookieDomain.isEmpty()) {
            builder.domain(cookieDomain);
        }

        ResponseCookie cookie = builder.build();
        response.addHeader("Set-Cookie", cookie.toString());
    }

    // Refresh Token 쿠키에서 tokenId 추출
    public Optional<String> getRefreshTokenFromCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (REFRESH_TOKEN_COOKIE_NAME.equals(cookie.getName())) {
                    return Optional.ofNullable(cookie.getValue());
                }
            }
        }
        return Optional.empty();
    }

    // Refresh Token 쿠키 삭제 (로그아웃 시)
    public void deleteRefreshTokenCookie(HttpServletResponse response) {
        ResponseCookie.ResponseCookieBuilder builder = ResponseCookie.from(REFRESH_TOKEN_COOKIE_NAME, "")
                .httpOnly(true)
                .secure(cookieSetsecure)
                .path("/")
                .maxAge(0) // 즉시 삭제
                .sameSite(cookieSameSite);

        if (cookieDomain != null && !cookieDomain.isEmpty()) {
            builder.domain(cookieDomain);
        }

        ResponseCookie cookie = builder.build();
        response.addHeader("Set-Cookie", cookie.toString());
    }
}