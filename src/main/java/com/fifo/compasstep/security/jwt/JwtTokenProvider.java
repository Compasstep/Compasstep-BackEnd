package com.fifo.compasstep.security.jwt;


import com.fifo.compasstep.security.userDetails.AdminUserDetails;
import com.fifo.compasstep.security.userDetails.UserUserDetails;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

    private final JwtProperties jwtProperties;
    private Key secretKey;
    private static final String USER_TYPE_KEY = "type"; // "type" 클레임 이름

    @PostConstruct
    protected void init() {
        String secret = jwtProperties.getSecret();
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Access Token 생성: 'type' 클레임에 '주요 권한' 문자열 저장
     */
    public String createAccessToken(Authentication authentication) {
        Date now = new Date();
        Date accessTokenExpiresIn = new Date(now.getTime() + jwtProperties.getAccessToken().getExpiration());

        Object principal = authentication.getPrincipal();
        String userId;
        String authorityString; // "ROLE_ROOT", "STATUS_NORMAL" 등 저장

        // --- ▼▼▼ Principal 타입 안전하게 확인 및 정보 추출 ▼▼▼ ---
        if (principal instanceof AdminUserDetails) {
            AdminUserDetails adminDetails = (AdminUserDetails) principal;
            userId = String.valueOf(adminDetails.getAdmin().getId());
            // AdminUserDetails에서 정의된 첫 번째 권한 가져오기
            authorityString = adminDetails.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("Admin has no authorities"));

        } else if (principal instanceof UserUserDetails) {
            UserUserDetails userDetails = (UserUserDetails) principal;
            userId = String.valueOf(userDetails.getUser().getId());
            // UserUserDetails에서 정의된 첫 번째 권한 가져오기
            authorityString = userDetails.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("User has no authorities"));

        } else {
            // 다른 타입의 Principal이 올 경우 예외 처리 (예: Spring의 기본 User 객체 등)
            // 필요하다면 여기서 다른 UserDetails 타입에 대한 처리 추가
            throw new IllegalArgumentException("지원되지 않는 Principal 타입입니다: " + principal.getClass().getName());
        }
        // --- ▲▲▲ ---

        return Jwts.builder()
                .setSubject(userId)                 // Subject에는 ID 저장
                .claim(USER_TYPE_KEY, authorityString) // "type" 클레임에 권한 문자열 저장
                .setIssuedAt(now)
                .setExpiration(accessTokenExpiresIn)
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Refresh Token 생성 (ID만 포함)
     */
    public String createRefreshToken(Authentication authentication) {
        Object principal = authentication.getPrincipal();
        String userId;
        if (principal instanceof AdminUserDetails) {
            userId = String.valueOf(((AdminUserDetails) principal).getAdmin().getId());
        } else if (principal instanceof UserUserDetails) {
            userId = String.valueOf(((UserUserDetails) principal).getUser().getId());
        } else {
            throw new IllegalArgumentException("지원되지 않는 Principal 타입입니다.");
        }

        Date now = new Date();
        Date refreshTokenExpiresIn = new Date(now.getTime() + jwtProperties.getRefreshToken().getExpiration());

        return Jwts.builder()
                .setSubject(userId)
                .setIssuedAt(now)
                .setExpiration(refreshTokenExpiresIn)
                .signWith(secretKey)
                .compact();
    }

    // getAuthentication 메소드는 DB 조회 방식에서는 불필요하므로 삭제

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private Claims parseClaims(String token) {
        try {
            return Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token).getBody();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        }
    }

    // 토큰에서 사용자 ID(PK) 추출
    public Long getUserId(String token) {
        return Long.parseLong(parseClaims(token).getSubject());
    }

    // 토큰에서 사용자 타입 ("ADMIN" 또는 "USER") 추출
    public String getUserType(String token) {
        return parseClaims(token).get(USER_TYPE_KEY, String.class);
    }
}

//@RequiredArgsConstructor
//public class JwtTokenProvider {
//    private final JwtProperties jwtProperties;
//
//    public String createAccessToken(String adminId) {
//        Claims claims = Jwts.claims().setSubject(adminId);
//        Date now = new Date();
//        Date accessTokenExpiresIn = new Date(now.getTime() + jwtProperties.getAccessToken().getExpiration());
//
//        SecretKey key = Keys.hmacShaKeyFor(jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8));
//
//        return Jwts.builder()
//                .setHeaderParam("typ", "JWT")
//                .setClaims(claims)
//                .setIssuedAt(now)
//                .setExpiration(accessTokenExpiresIn)
//                .signWith(key, SignatureAlgorithm.forName(jwtProperties.getAlgorithm()))
//                .compact();
//    }
//
//    public String createRefreshToken(String adminId) {
//        Date now = new Date();
//        Date refreshTokenExpiresIn = new Date(now.getTime() + jwtProperties.getRefreshToken().getExpiration());
//
//        return Jwts.builder()
//                .setSubject(adminId)
//                .setIssuedAt(now)
//                .setExpiration(refreshTokenExpiresIn)
//                .signWith(Keys.hmacShaKeyFor(jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8)),
//                        SignatureAlgorithm.forName(jwtProperties.getAlgorithm()))
//                .compact();
//    }
//
//    public boolean validateToken(String token) {
//        try {
//            SecretKey key = Keys.hmacShaKeyFor(jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8));
//            Jwts.parserBuilder()
//                    .setSigningKey(key)
//                    .build()
//                    .parseClaimsJws(token);
//            return true;
//        } catch (Exception e) {
//            return false;
//        }
//    }
//
//    // 토큰에서 관리자 아이디 추출
//    public String getAdminIdFromToken(String token) {
//        Claims claims = getClaimsFromToken(token);
//        return claims.getSubject();
//    }
//
//    // 토큰에서 모든 Claims 추출
//    private Claims getClaimsFromToken(String token) {
//        return Jwts.parserBuilder()
//                .setSigningKey(Keys.hmacShaKeyFor(jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8)))
//                .build()
//                .parseClaimsJws(token)
//                .getBody();
//    }
//
//
////    public AdminProfileDTO getUserTokenInfo(String token) {
////        Claims claims = getClaimsFromToken(token);
////        return AdminProfileDTO.builder()
////                .adminId(claims.getSubject())
////                .permission((String) claims.get("permission"))
////                .organizationId((String) claims.get("organizationId"))
////                .build();
////    }
//}
