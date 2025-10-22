package com.fifo.compasstep.admin.service;

import com.fifo.compasstep.admin.domain.Admin;
import com.fifo.compasstep.admin.enums.Role; // Role Enum import
import com.fifo.compasstep.admin.dto.AdminRequestDTO;
import com.fifo.compasstep.admin.dto.AdminResponseDTO;
import com.fifo.compasstep.admin.exceptions.AdminErrorStatus;
import com.fifo.compasstep.admin.repository.AdminRepository;
import com.fifo.compasstep.apipayload.exceptions.handler.AdminHandler;
import com.fifo.compasstep.security.jwt.JwtProperties;
import com.fifo.compasstep.security.jwt.JwtTokenProvider;
import com.fifo.compasstep.security.service.RefreshTokenService;
import com.fifo.compasstep.security.userDetails.AdminUserDetails;
import com.fifo.compasstep.security.userDetails.UserUserDetails; // UserUserDetails import
import com.fifo.compasstep.security.util.CookieUtil;
import com.fifo.compasstep.user.domain.User; // User import
import com.fifo.compasstep.user.repository.UserRepository; // UserRepository import
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j; // Slf4j import
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails; // UserDetails import
import org.springframework.security.core.userdetails.UsernameNotFoundException; // Exception import
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Slf4j // 로그 사용을 위해 추가
@Service
@RequiredArgsConstructor
public class AdminService {
    private final AdminRepository adminRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final JwtProperties jwtProperties;
    private final CookieUtil cookieUtil;
    private final AuthenticationManager authenticationManager;
    private final RefreshTokenService refreshTokenService;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final UserRepository userRepository; // refreshToken 로직에서 User 조회용

    @Transactional
    public AdminResponseDTO.LoginResponseDTO login(AdminRequestDTO.AdminLoginRequestDTO request, HttpServletResponse response) {
        // 1. 사용자 인증 (성공 시 Authentication 객체 반환)
        Authentication authentication = authenticateUser(request);
        Admin admin = ((AdminUserDetails) authentication.getPrincipal()).getAdmin();

        // 2. 토큰 생성 및 쿠키 설정
        return issueTokensAndSetCookies(admin.getId(), "ADMIN", authentication, response);
    }

    private Authentication authenticateUser(AdminRequestDTO.AdminLoginRequestDTO request) {
        try {
            return authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );
        } catch (Exception e) {
            log.error("Admin authentication failed for email: {}", request.getEmail(), e); // 실패 로그 추가
            throw new AdminHandler(AdminErrorStatus.ADMIN_LOGIN_FAILED);
        }
    }

    /**
     * 인증 성공 후 토큰 발급 및 쿠키 설정을 처리하는 공통 로직 (AdminService 내부에 유지)
     */
    private AdminResponseDTO.LoginResponseDTO issueTokensAndSetCookies(Long userId, String userType, Authentication authentication, HttpServletResponse response) {
        // 1. 기존 Refresh Token 삭제
        refreshTokenService.removeRefreshTokenByUser(userId, userType);

        // 2. 새로운 Access/Refresh Token 생성
        String accessToken = jwtTokenProvider.createAccessToken(authentication);
        String refreshToken = jwtTokenProvider.createRefreshToken(authentication);

        // 3. Refresh Token을 Redis에 저장
        String tokenId = refreshTokenService.storeRefreshToken(
                userId,
                userType,
                refreshToken,
                jwtProperties.getRefreshToken().getExpiration()
        );

        // 4. 쿠키 설정
        setCookies(response, accessToken, tokenId);
        String csrfToken = createCsrfToken(response);

        return AdminResponseDTO.LoginResponseDTO.builder()
                .csrfToken(csrfToken)
                .build();
    }

    private void setCookies(HttpServletResponse response, String accessToken, String tokenId) {
        cookieUtil.createAccessTokenCookie(response, accessToken);
        cookieUtil.createRefreshTokenCookie(response, tokenId);
    }

    private String createCsrfToken(HttpServletResponse response) {
        return cookieUtil.createCsrfTokenCookie(response);
    }

    @Transactional
    public AdminResponseDTO.LoginResponseDTO refreshToken(HttpServletRequest request, HttpServletResponse response) {
        // 1. 쿠키에서 Refresh Token ID (tokenId) 추출
        String tokenId = cookieUtil.getRefreshTokenFromCookie(request)
                .orElseThrow(() -> new AdminHandler(AdminErrorStatus.REFRESH_TOKEN_NOT_FOUND));

        // 2. Redis에서 RefreshTokenInfo 객체 조회
        RefreshTokenService.RefreshTokenInfo tokenInfo = refreshTokenService.getRefreshTokenInfo(tokenId)
                .orElseThrow(() -> new AdminHandler(AdminErrorStatus.REFRESH_TOKEN_EXPIRED)); // 만료 또는 없음

        String refreshToken = tokenInfo.getRefreshToken();
        Long userId = tokenInfo.getUserId();
        String userType = tokenInfo.getUserType();

        // 3. Refresh Token 자체의 유효성 검증
        if (!jwtTokenProvider.validateToken(refreshToken)) {
            refreshTokenService.removeRefreshToken(tokenId); // 유효하지 않으면 Redis에서도 삭제
            throw new AdminHandler(AdminErrorStatus.REFRESH_TOKEN_INVALID);
        }

        // 4. userType에 따라 DB에서 실제 사용자 조회 및 UserDetails 생성
        UserDetails userDetails;
        if ("ADMIN".equals(userType) || "ROOT".equals(userType)) {
            Admin admin = adminRepository.findById(userId)
                    .orElseThrow(() -> new AdminHandler(AdminErrorStatus.ADMIN_NOT_FOUND));

            userDetails = new AdminUserDetails(admin);
        } else if ("USER".equals(userType)) {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new AdminHandler(AdminErrorStatus.ADMIN_NOT_FOUND)); // User용 예외 사용 고려
            userDetails = new UserUserDetails(user);
        } else {
            // Redis에 잘못된 userType이 저장된 경우
            log.error("Invalid user type found in RefreshTokenInfo: {}", userType);
            throw new AdminHandler(AdminErrorStatus.NOT_SUPPORTED_USERTYPE);
        }

        // 5. DB에서 조회한 UserDetails를 바탕으로 '새로운' Authentication 객체 생성
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities()
        );

        // 6. 새로운 토큰 발급 및 쿠키 설정 (공통 로직 재사용)
        // 주의: refreshToken 메소드 내에서 issueTokensAndSetCookies를 호출하면
        // removeRefreshTokenByUser가 두 번 호출될 수 있으므로,
        // 토큰 생성 및 저장/쿠키 설정 로직을 여기에 직접 다시 구현하거나 별도 private 메소드로 분리

        // 토큰 생성 및 저장/쿠키 설정 로직 (issueTokensAndSetCookies와 유사)
        String newAccessToken = jwtTokenProvider.createAccessToken(authentication);
        String newRefreshToken = jwtTokenProvider.createRefreshToken(authentication);

        try {
            refreshTokenService.removeRefreshToken(tokenId); // 기존 tokenId로 삭제
            String newTokenId = refreshTokenService.storeRefreshToken(
                    userId, // Redis에서 가져온 userId
                    userType, // Redis에서 가져온 userType
                    newRefreshToken,
                    jwtProperties.getRefreshToken().getExpiration()
            );

            setCookies(response, newAccessToken, newTokenId); // 헬퍼 메소드 사용
            String csrfToken = createCsrfToken(response); // 헬퍼 메소드 사용

            return AdminResponseDTO.LoginResponseDTO.builder()
                    .csrfToken(csrfToken)
                    .build();
        } catch (Exception e) {
            log.error("Error occurred during token refresh process", e);
            throw new AdminHandler(AdminErrorStatus.REDIS_ERROR);
        }
    }

    public void logout(HttpServletRequest request, HttpServletResponse response) {
        Optional<String> tokenIdOptional = cookieUtil.getRefreshTokenFromCookie(request);
        tokenIdOptional.ifPresent(tokenId -> {
            refreshTokenService.removeRefreshToken(tokenId);
        });

        cookieUtil.deleteAccessTokenCookie(response);
        cookieUtil.deleteRefreshTokenCookie(response);
        cookieUtil.deleteCsrfTokenCookie(response);
    }

    @Transactional
    public void deleteAdmin(Long currentAdminId, Long adminPKId) {
        if (Objects.equals(currentAdminId, adminPKId)) {
            throw new AdminHandler(AdminErrorStatus.CANNOT_DELETE_SELF);
        }
        Admin adminToDelete = adminRepository.findById(adminPKId)
                .orElseThrow(() -> new AdminHandler(AdminErrorStatus.ADMIN_NOT_FOUND));

        adminToDelete.anonymize(); // anonymize 메소드 호출 (소프트 삭제)
        adminRepository.save(adminToDelete); // 변경사항 저장

        refreshTokenService.removeRefreshTokenByUser(adminToDelete.getId(), "ADMIN");
    }
        tempPassword.put("encoded", passwordEncoder.encode(temp));
        return tempPassword;
    }
}
