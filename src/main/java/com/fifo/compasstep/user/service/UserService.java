package com.fifo.compasstep.user.service;

import com.fifo.compasstep.apipayload.exceptions.handler.UserHandler;
import com.fifo.compasstep.security.jwt.JwtProperties;
import com.fifo.compasstep.security.jwt.JwtTokenProvider;
import com.fifo.compasstep.security.service.RefreshTokenService;

import com.fifo.compasstep.security.userDetails.UserUserDetails;
import com.fifo.compasstep.security.util.CookieUtil;
import com.fifo.compasstep.user.domain.User;

import com.fifo.compasstep.user.dto.GoogleUserInfo;
import com.fifo.compasstep.user.dto.UserRequestDTO;
import com.fifo.compasstep.user.dto.UserResponseDTO;
import com.fifo.compasstep.user.exceptions.UserErrorStatus;
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
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

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
                    User newUser = User.builder()
                            .email(googleUserInfo.getEmail())
                            .name(googleUserInfo.getName())
                            .build();
                    return userRepository.save(newUser);
                });

        // 3. 토큰 생성 및 쿠키 설정
        return createTokensAndSetCookies(user, response);
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

    @Transactional
    public void signout(HttpServletRequest request, HttpServletResponse response) {
        // 현재 로그인된 사용자 정보 가져오기
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof UserUserDetails)) {
            // 인증 정보가 없거나, 예상치 못한 Principal 타입인 경우 예외 처리
            throw new UserHandler(UserErrorStatus.USER_NOT_FOUND); // 적절한 에러 상태 정의 필요
        }
        // 현재 로그인 된 사용자를 가져온 뒤 익명화
        UserUserDetails currentUserDetails = (UserUserDetails) authentication.getPrincipal();
        Long currentUserId = currentUserDetails.getUser().getId();
        // 2. ID를 사용해 DB에서 User 객체를 '다시 조회'하여 managed 상태로 만듦
        User currentUser = userRepository.findById(currentUserId)
                .orElseThrow(() -> new UserHandler(UserErrorStatus.USER_NOT_FOUND));
        currentUser.anonymize();

        logout(request, response);
    }

    public UserResponseDTO.generatePresignedUrlResponseDTO generateUrl(
            UserRequestDTO.generatePresignedUrlRequestDTO request, HttpServletResponse response) {
        return s3Service.generatePresignedUrl(request);
    }

    public UserResponseDTO.downloadUrlResponseDTO generateDownloadUrl(
            UserRequestDTO.downloadUrlRequestDTO request){
        String presignedUrl = s3Service.generatePresignedUrlForDownload(request.getFileKey(), request.getOriginalFileName(), 3600);

        return UserResponseDTO.downloadUrlResponseDTO.builder()
                .presignedUrl(presignedUrl)
                .build();
    }

}