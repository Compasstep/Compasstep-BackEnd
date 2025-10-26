package com.fifo.compasstep.user.service;

import com.fifo.compasstep.security.jwt.JwtProperties;
import com.fifo.compasstep.security.jwt.JwtTokenProvider;
import com.fifo.compasstep.security.service.RefreshTokenService;

import com.fifo.compasstep.security.userDetails.UserUserDetails;
import com.fifo.compasstep.security.util.CookieUtil;
import com.fifo.compasstep.user.domain.User;

import com.fifo.compasstep.user.dto.GoogleUserInfo;
import com.fifo.compasstep.user.dto.UserRequestDTO;
import com.fifo.compasstep.user.dto.UserResponseDTO;
import com.fifo.compasstep.user.enums.Status;
import com.fifo.compasstep.user.repository.UserRepository;
import com.fifo.compasstep.user.tokenVerifier.GoogleTokenVerifier;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final JwtProperties jwtProperties;
    private final RefreshTokenService refreshTokenService;
    private final CookieUtil cookieUtil;
    private final JwtTokenProvider jwtTokenProvider;
    private final GoogleTokenVerifier googleTokenVerifier;
    private final S3Service s3Service;

    @Transactional
    public UserResponseDTO.LoginResponseDTO login(UserRequestDTO.LoginRequestDTO request, HttpServletResponse response) {
        // 1. 구글 토큰 검증 및 사용자 정보 가져오기
        String googleToken = request.getGoogleToken().replace("Bearer ", "");
        GoogleUserInfo googleUserInfo = googleTokenVerifier.verify(googleToken);

        // 2. 사용자 조회 또는 신규 생성
        User user = userRepository.findByEmail(googleUserInfo.getEmail())
                .orElseGet(() -> {
                    // ★ 닉네임 생성 (이름 우선, 없으면 이메일 로컬파트)
                    String nickname = buildNickname(googleUserInfo.getName(), googleUserInfo.getEmail());

                    User newUser = User.builder()
                            .email(googleUserInfo.getEmail())
                            .name(googleUserInfo.getName())
                            .nickname(nickname)                    // ★ 반드시 세팅
                            .status(Status.NORMAL)           // (엔티티/DB 기본값 있으면 생략)
                            .build();
                    return userRepository.save(newUser);
                });

        //  nickname이 비었으면 지금 채워 저장
        if (user.getNickname() == null || user.getNickname().isBlank()) {
            user.setNickname(buildNickname(user.getName(), user.getEmail()));
            userRepository.save(user);
        }

        // 3. 토큰 생성 및 쿠키 설정
        return createTokensAndSetCookies(user, response);
    }

    /** 닉네임 생성 */
    private String buildNickname(String name, String email) {
        String base = (name != null && !name.isBlank())
                ? name
                : (email != null ? email.split("@")[0] : "user");
        // 공백→밑줄, 허용문자만 남기기(영문/숫자/한글/밑줄)
        String cleaned = base.replaceAll("\\s+", "_")
                .replaceAll("[^0-9A-Za-z가-힣_]", "");
        if (cleaned.isBlank()) cleaned = "user";
        // 너무 길면 잘라주기(선택)
        if (cleaned.length() > 20) cleaned = cleaned.substring(0, 20);
        return cleaned;
    }


    /**
     * 토큰을 생성하고 쿠키를 설정합니다. (UserService 내부에 다시 구현)
     */
    private UserResponseDTO.LoginResponseDTO createTokensAndSetCookies(User user, HttpServletResponse response) {

        // 1. Authentication 객체 생성
        List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_USER"));
        UserUserDetails userDetails = new UserUserDetails(user);
        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, authorities);

        // 2. 기존 Refresh Token 삭제 (ID와 타입 사용)
        refreshTokenService.removeRefreshTokenByUser(user.getId(), "USER");

        // 3. Access/Refresh Token 생성 (Authentication 객체 전달)
        String accessToken = jwtTokenProvider.createAccessToken(authentication);
        String refreshToken = jwtTokenProvider.createRefreshToken(authentication);

        // 4. Refresh Token을 Redis에 저장 (ID와 타입 사용)
        String tokenId = refreshTokenService.storeRefreshToken(
                user.getId(),
                "USER",
                refreshToken,
                jwtProperties.getRefreshToken().getExpiration()
        );


        // 5. 쿠키 설정
        setCookies(response, accessToken, tokenId);
        String csrfToken = createCsrfToken(response);

        return UserResponseDTO.LoginResponseDTO.builder()
                .csrfToken(csrfToken)
                .build();
    }

    // --- 아래는 AdminService와 중복되는 헬퍼 메소드들 ---

    private void setCookies(HttpServletResponse response, String accessToken, String tokenId) {
        cookieUtil.createAccessTokenCookie(response, accessToken);
        cookieUtil.createRefreshTokenCookie(response, tokenId);
    }

    private String createCsrfToken(HttpServletResponse response) {
        return cookieUtil.createCsrfTokenCookie(response);
    }

    public void logout(HttpServletRequest request, HttpServletResponse response) {
        cookieUtil.getRefreshTokenFromCookie(request)
                .ifPresent(tokenId -> refreshTokenService.removeRefreshToken(tokenId));

        cookieUtil.deleteAccessTokenCookie(response);
        cookieUtil.deleteRefreshTokenCookie(response);
        cookieUtil.deleteCsrfTokenCookie(response);
    }

    public UserResponseDTO.generatePresignedUrlResponseDTO generateUrl(
            UserRequestDTO.generatePresignedUrlRequestDTO request, HttpServletResponse response) {
        return s3Service.generatePresignedUrl(request);
    }
}
