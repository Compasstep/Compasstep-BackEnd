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

    /* ============================================================
       🔥 로그인 – JWT + CSRF 쿠키 생성
    ============================================================ */
    @Transactional
    public UserResponseDTO.LoginResponseDTO login(
            UserRequestDTO.LoginRequestDTO request,
            HttpServletResponse response) {

        String googleToken = request.getGoogleToken().replace("Bearer ", "");
        GoogleUserInfo googleUserInfo = googleTokenVerifier.verify(googleToken);

        User user = userRepository.findByEmail(googleUserInfo.getEmail())
                .orElseGet(() -> userRepository.save(
                        User.builder()
                                .email(googleUserInfo.getEmail())
                                .name(googleUserInfo.getName())
                                .build()
                ));

        return createTokensAndSetCookies(user, response);
    }


    /* ============================================================
       🔥 토큰 및 쿠키 생성
    ============================================================ */
    private UserResponseDTO.LoginResponseDTO createTokensAndSetCookies(
            User user,
            HttpServletResponse response
    ) {

        UserUserDetails userDetails = new UserUserDetails(user);

        List<GrantedAuthority> authorities =
                List.of(new SimpleGrantedAuthority("STATUS_NORMAL"));

        Authentication auth =
                new UsernamePasswordAuthenticationToken(userDetails, null, authorities);

        refreshTokenService.removeRefreshTokenByUser(user.getId(), "USER");

        String accessToken = jwtTokenProvider.createAccessToken(auth);
        String refreshToken = jwtTokenProvider.createRefreshToken(auth);

        String tokenId = refreshTokenService.storeRefreshToken(
                user.getId(),
                "USER",
                refreshToken,
                jwtProperties.getRefreshToken().getExpiration()
        );

        cookieUtil.createAccessTokenCookie(response, accessToken);
        cookieUtil.createRefreshTokenCookie(response, tokenId);
        String csrfToken = cookieUtil.createCsrfTokenCookie(response);

        return UserResponseDTO.LoginResponseDTO.builder()
                .csrfToken(csrfToken)
                .build();
    }


    /* ============================================================
       🔥 로그아웃 – 쿠키/토큰 삭제
    ============================================================ */
    public void logout(HttpServletRequest request, HttpServletResponse response) {

        cookieUtil.getRefreshTokenFromCookie(request)
                .ifPresent(tokenId -> refreshTokenService.removeRefreshToken(tokenId));

        cookieUtil.deleteAccessTokenCookie(response);
        cookieUtil.deleteRefreshTokenCookie(response);
        cookieUtil.deleteCsrfTokenCookie(response);
    }


    /* ============================================================
       🔥 회원탈퇴(익명화) + 로그아웃
    ============================================================ */
    @Transactional
    public void signout(HttpServletRequest request, HttpServletResponse response) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (!(auth.getPrincipal() instanceof UserUserDetails userDetails)) {
            throw new UserHandler(UserErrorStatus.USER_NOT_FOUND);
        }

        Long userId = userDetails.getUser().getId();

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserHandler(UserErrorStatus.USER_NOT_FOUND));

        user.anonymize();

        logout(request, response);
    }


    /* ============================================================
       🔥 Presigned URL 생성
    ============================================================ */
    public UserResponseDTO.generatePresignedUrlResponseDTO generateUrl(
            UserRequestDTO.generatePresignedUrlRequestDTO request) {

        return s3Service.generatePresignedUrl(request);
    }


    /* ============================================================
       🔥 파일 다운로드 URL 생성
    ============================================================ */
    public UserResponseDTO.downloadUrlResponseDTO generateDownloadUrl(
            UserRequestDTO.downloadUrlRequestDTO request) {

        String presignedUrl = s3Service.generatePresignedUrlForDownload(
                request.getFileKey(),
                request.getOriginalFileName(),
                3600
        );

        return UserResponseDTO.downloadUrlResponseDTO.builder()
                .presignedUrl(presignedUrl)
                .build();
    }
}
