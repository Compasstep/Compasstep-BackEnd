package com.fifo.compasstep.security.filter;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fifo.compasstep.admin.domain.Admin;
import com.fifo.compasstep.admin.exceptions.AdminErrorStatus;
import com.fifo.compasstep.admin.repository.AdminRepository;
import com.fifo.compasstep.apipayload.ApiResponse;
import com.fifo.compasstep.security.jwt.JwtTokenProvider;
import com.fifo.compasstep.security.userDetails.AdminUserDetails;
import com.fifo.compasstep.security.userDetails.UserUserDetails;
import com.fifo.compasstep.security.util.CookieUtil;
import com.fifo.compasstep.user.domain.User;
import com.fifo.compasstep.user.repository.UserRepository;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final CookieUtil cookieUtil;
    // UserDetailsService 대신, 각 Repository를 직접 주입받아 ID로 조회합니다.
    private final AdminRepository adminRepository;
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {


        // Swagger 관련 경로는 JWT 검증 건너뛰기
        String requestURI = request.getRequestURI();
        if (requestURI.equals("/swagger-ui.html") ||
                requestURI.startsWith("/swagger-ui/") || // '/'를 붙여 /swagger-ui.html과 구분
                requestURI.startsWith("/v3/api-docs") ||
                requestURI.startsWith("/api-docs") ||
                requestURI.startsWith("/swagger-resources") || // 스웨거 리소스 추가
                // --- 기존 관리자/유저 경로 ---
                requestURI.startsWith("/api/admin/login") ||
                requestURI.startsWith("/api/admin/signup") ||
                requestURI.startsWith("/api/admin/verify") ||
                requestURI.startsWith("/api/admin/refresh") ||
                requestURI.startsWith("/api/admin/logout") ||
                requestURI.startsWith("/api/user/auth/login") ||
                requestURI.startsWith("/api/user/files/download") ||
                requestURI.startsWith("/api/user/auth/logout")) {

            filterChain.doFilter(request, response);
            return;
        }
        // 1. Access Token을 쿠키에서 추출 (기존 코드와 동일)
        // getAccessTokenFromCookie가 null을 반환할 수 있으므로, null 체크를 합니다.
        String token = cookieUtil.getAccessTokenFromCookie(request);

        // 2. 토큰 유효성 검사
        if (token != null) {
            try {
                if (jwtTokenProvider.validateToken(token)) {
                    // --- ▼▼▼ 여기가 핵심 변경 로직 ▼▼▼ ---

                    // 3. 토큰에서 사용자 ID와 타입 추출
                    Long userId = jwtTokenProvider.getUserId(token);
                    String userType = jwtTokenProvider.getUserType(token);

                    UserDetails userDetails;

                    // 4. 사용자 타입에 따라 적절한 Repository를 사용하여 DB에서 최신 정보 조회
                    if ("ROLE_ROOT".equals(userType) || "ROLE_GENERAL".equals(userType) ||
                            ("ROLE_DELETED".equals(userType))) {
                        Admin admin = adminRepository.findById(userId)
                                .orElseThrow(() -> new UsernameNotFoundException("Admin not found with id: " + userId));
                        // AdminUserDetails를 사용하여 UserDetails 생성
                        userDetails = new AdminUserDetails(admin);
                    } else if ("STATUS_NORMAL".equals(userType) || "STATUS_SUSPENDED".equals(userType) ||
                            ("STATUS_BLOCKED".equals(userType) || "STATUS_DELETED".equals(userType))) {
                        User user = userRepository.findById(userId)
                                .orElseThrow(() -> new UsernameNotFoundException("User not found with id: " + userId));
                        // CustomUserDetails를 사용하여 UserDetails 생성
                        userDetails = new UserUserDetails(user);
                    } else {
                        throw new IllegalStateException("Invalid user type in token: " + userType);
                    }

                    // --- ▲▲▲ 여기까지 핵심 변경 로직 ▲▲▲ ---

                    // 5. 인증 객체 생성 및 SecurityContext에 등록 (기존 코드와 동일)
                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails,
                                    null,
                                    userDetails.getAuthorities()
                            );
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            } catch (ExpiredJwtException e) {
                // 토큰 만료 시 커스텀 응답 (기존 코드와 동일)
                handleException(response, AdminErrorStatus.JWT_ADMIN_INVALID_TOKEN); // 에러 상태는 필요에 맞게 수정
                return;
            } catch (UsernameNotFoundException e) {
                //유저 못찾을 경우 커스텀 응답
                handleException(response, AdminErrorStatus.ERROR_FOUR);
                return;
            } catch (Exception e) {
                // 기타 JWT 관련 오류 (기존 코드와 동일)
                e.printStackTrace(); // 실제 오류의 전체 내용을 콘솔에 출력합니다.
                handleException(response, AdminErrorStatus.ANOTHER_ERROR);
                return;
            }
        }

        // 6. 다음 필터로 진행
        filterChain.doFilter(request, response);
    }

    // 예외 처리 로직을 별도 메서드로 분리
    private void handleException(HttpServletResponse response, AdminErrorStatus errorStatus) throws IOException {
        ApiResponse<Object> apiResponse = ApiResponse.onFailure(
                errorStatus.getCode(),
                errorStatus.getMessage(),
                null
        );
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(objectMapper.writeValueAsString(apiResponse));
    }
}
