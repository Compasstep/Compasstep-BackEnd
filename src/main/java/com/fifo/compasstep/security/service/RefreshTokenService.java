package com.fifo.compasstep.security.service;


import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.time.Duration;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {
    private final RedisTemplate<String, Object> redisTemplate;

    private static final String REFRESH_TOKEN_PREFIX = "refresh_token:";
    private static final String USER_TOKEN_PREFIX = "user_token:";

    /**
     * Refresh Token을 저장하고 고유 ID(tokenId)를 반환합니다.
     * @param userId 사용자(Admin 또는 User)의 고유 PK
     * @param userType 사용자의 타입 ("ADMIN" 또는 "USER")
     * @param refreshToken 저장할 Refresh Token
     * @param expirationMillis 만료 시간
     * @return Redis에 저장된 토큰의 고유 ID (UUID)
     */
    public String storeRefreshToken(Long userId, String userType, String refreshToken, long expirationMillis) {
        String tokenId = UUID.randomUUID().toString();
        String tokenKey = REFRESH_TOKEN_PREFIX + tokenId;
        String userKey = USER_TOKEN_PREFIX + userType + ":" + userId; // 예: "user_token:ADMIN:1"

        RefreshTokenInfo tokenInfo = new RefreshTokenInfo(userId, userType, refreshToken);

        redisTemplate.opsForValue().set(tokenKey, tokenInfo, Duration.ofMillis(expirationMillis));
        redisTemplate.opsForValue().set(userKey, tokenId, Duration.ofMillis(expirationMillis));

        return tokenId;
    }

    /**
     * Token ID로 Refresh Token 정보를 조회합니다. (로그아웃 시 사용)
     * @param tokenId 조회할 토큰의 ID (UUID)
     * @return RefreshTokenInfo 객체
     */
    public Optional<RefreshTokenInfo> getRefreshTokenInfo(String tokenId) {
        String tokenKey = REFRESH_TOKEN_PREFIX + tokenId;
        RefreshTokenInfo tokenInfo = (RefreshTokenInfo) redisTemplate.opsForValue().get(tokenKey);
        return Optional.ofNullable(tokenInfo);
    }

    /**
     * Token ID로 Refresh Token 문자열을 직접 조회합니다.
     * @param tokenId 조회할 토큰의 ID (UUID)
     * @return Refresh Token 문자열
     */
    public Optional<String> getRefreshToken(String tokenId) {
        return getRefreshTokenInfo(tokenId).map(RefreshTokenInfo::getRefreshToken);
    }

    /**
     * 로그아웃 시 Token ID를 사용하여 관련된 모든 토큰 정보를 삭제합니다.
     * @param tokenId 삭제할 토큰의 ID (UUID)
     */
    public void removeRefreshToken(String tokenId) {
        getRefreshTokenInfo(tokenId).ifPresent(tokenInfo -> {
            String tokenKey = REFRESH_TOKEN_PREFIX + tokenId;
            String userKey = USER_TOKEN_PREFIX + tokenInfo.getUserType() + ":" + tokenInfo.getUserId();
            redisTemplate.delete(tokenKey);
            redisTemplate.delete(userKey);
        });
    }

    /**
     * 새로운 로그인 시, 기존에 발급된 토큰을 무효화하기 위해 삭제합니다.
     * @param userId 사용자(Admin 또는 User)의 고유 PK
     * @param userType 사용자의 타입 ("ADMIN" 또는 "USER")
     */
    public void removeRefreshTokenByUser(Long userId, String userType) {
        String userKey = USER_TOKEN_PREFIX + userType + ":" + userId;
        String tokenId = (String) redisTemplate.opsForValue().get(userKey);

        if (tokenId != null) {
            removeRefreshToken(tokenId);
        }
    }

    // 내부 클래스: Refresh Token 정보를 담는 클래스
    // 필드 수정 및 추가 (email -> userId, userType)
    private static class RefreshTokenInfo implements Serializable {
        private Long userId;
        private String userType;
        private String refreshToken;

        public RefreshTokenInfo() {}

        public RefreshTokenInfo(Long userId, String userType, String refreshToken) {
            this.userId = userId;
            this.userType = userType;
            this.refreshToken = refreshToken;
        }

        // Getters & Setters
        public Long getUserId() { return userId; }
        public void setUserId(Long userId) { this.userId = userId; }
        public String getUserType() { return userType; }
        public void setUserType(String userType) { this.userType = userType; }
        public String getRefreshToken() { return refreshToken; }
        public void setRefreshToken(String refreshToken) { this.refreshToken = refreshToken; }
    }
}

//@Service
//@RequiredArgsConstructor
//public class RefreshTokenService {
//    private final RedisTemplate<String, Object> redisTemplate;
//
//    private static final String REFRESH_TOKEN_PREFIX = "refresh_token:";
//    private static final String USER_TOKEN_PREFIX = "user_token:";
//
//    // refresh token을 저장하고 고유 ID를 반환
//    public String storeRefreshToken(String email, String refreshToken, long expirationMillis) {
//        String tokenId = UUID.randomUUID().toString();
//        String tokenKey = REFRESH_TOKEN_PREFIX + tokenId;
//        String userKey = USER_TOKEN_PREFIX + email;
//
//        // RefreshTokenInfo 객체 생성
//        RefreshTokenInfo tokenInfo = new RefreshTokenInfo(email, refreshToken);
//
//        // Redis에 저장 (만료 시간 설정)
//        redisTemplate.opsForValue().set(tokenKey, tokenInfo, Duration.ofMillis(expirationMillis));
//
//        // userId와 tokenId 매핑 저장 (기존 토큰 무효화용)
//        redisTemplate.opsForValue().set(userKey, tokenId, Duration.ofMillis(expirationMillis));
//
//        return tokenId;
//    }
//
//    // tokenId로 refresh token 조회
//    public Optional<String> getRefreshToken(String tokenId) {
//        String tokenKey = REFRESH_TOKEN_PREFIX + tokenId;
//        RefreshTokenInfo tokenInfo = (RefreshTokenInfo) redisTemplate.opsForValue().get(tokenKey);
//
//        if (tokenInfo == null) {
//            return Optional.empty();
//        }
//
//        return Optional.of(tokenInfo.getRefreshToken());
//    }
//
//    // adminId로 refresh token 조회 (로그인 시 기존 토큰 무효화용)
//    public String getTokenIdByAdminId(String email) {
//        String userKey = USER_TOKEN_PREFIX + email;
//        return (String) redisTemplate.opsForValue().get(userKey);
//    }
//
//    // refresh token 삭제 (로그아웃 시)
//    public void removeRefreshToken(String tokenId) {
//        String tokenKey = REFRESH_TOKEN_PREFIX + tokenId;
//        RefreshTokenInfo tokenInfo = (RefreshTokenInfo) redisTemplate.opsForValue().get(tokenKey);
//
//        if (tokenInfo != null) {
//            // tokenId로 저장된 토큰 삭제
//            redisTemplate.delete(tokenKey);
//
//            // adminId 매핑도 삭제
//            String userKey = USER_TOKEN_PREFIX + tokenInfo.getEmail();
//            redisTemplate.delete(userKey);
//        }
//    }
//
//    // adminId로 기존 refresh token 삭제 (새 로그인 시)
//    public void removeRefreshTokenByEmail(String email) {
//        String userKey = USER_TOKEN_PREFIX + email;
//        String tokenId = (String) redisTemplate.opsForValue().get(userKey);
//
//        if (tokenId != null) {
//            String tokenKey = REFRESH_TOKEN_PREFIX + tokenId;
//            redisTemplate.delete(tokenKey);
//            redisTemplate.delete(userKey);
//        }
//    }
//
//    // 내부 클래스: Refresh Token 정보를 담는 클래스
//    public static class RefreshTokenInfo {
//        private String email;
//        private String refreshToken;
//
//        // 기본 생성자 (Redis 직렬화를 위해 필요)
//        public RefreshTokenInfo() {}
//
//        public RefreshTokenInfo(String email, String refreshToken) {
//            this.email = email;
//            this.refreshToken = refreshToken;
//        }
//
//        // Getter와 Setter (Redis 직렬화를 위해 필요)
//        public String getEmail() { return email; }
//        public void setEmail(String email) { this.email = email; }
//
//        public String getRefreshToken() { return refreshToken; }
//        public void setRefreshToken(String refreshToken) { this.refreshToken = refreshToken; }
//
//    }
//}