package com.fifo.compasstep.security.jwt;


import com.fifo.compasstep.security.jwt.JwtProperties;
import com.fifo.compasstep.security.userDetails.AdminUserDetails;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

    private final JwtProperties jwtProperties;
    private Key secretKey;
    private static final String USER_TYPE_KEY = "type";

    @PostConstruct
    protected void init() {
        String secret = jwtProperties.getSecret();
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public String createAccessToken(Authentication authentication) {
        // 1. getPrincipal()은 Object를 반환하므로 AdminUserDetails로 타입 캐스팅합니다.
        AdminUserDetails adminDetails = (AdminUserDetails) authentication.getPrincipal();

        String authorities = adminDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        Date now = new Date();
        Date accessTokenExpiresIn = new Date(now.getTime() + jwtProperties.getAccessToken().getExpiration());

        String adminId = String.valueOf(adminDetails.getAdmin().getId());

        return Jwts.builder()
                .setSubject(adminId)
                .claim(USER_TYPE_KEY, authorities)
                .setIssuedAt(now)
                .setExpiration(accessTokenExpiresIn)
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    public String createRefreshToken(Authentication authentication) {
        AdminUserDetails adminDetails = (AdminUserDetails) authentication.getPrincipal();
        String adminId = String.valueOf(adminDetails.getAdmin().getId());

        Date now = new Date();
        Date refreshTokenExpiresIn = new Date(now.getTime() + jwtProperties.getRefreshToken().getExpiration());

        return Jwts.builder()
                .setSubject(adminId)
                .setIssuedAt(now)
                .setExpiration(refreshTokenExpiresIn)
                .signWith(secretKey)
                .compact();
    }

    public Authentication getAuthentication(String token) {
        Claims claims = parseClaims(token);

        if (claims.get(USER_TYPE_KEY) == null) {
            throw new RuntimeException("권한 정보가 없는 토큰입니다.");
        }

        Collection<? extends GrantedAuthority> authorities =
                Arrays.stream(claims.get(USER_TYPE_KEY).toString().split(","))
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());

        // 2. User 생성자의 세 번째 인자는 Collection 타입이므로 List를 그대로 전달합니다.
        UserDetails principal = new User(claims.getSubject(), "", authorities);

        // 3. UsernamePasswordAuthenticationToken은 Authentication의 구현체이므로 반환 타입과 호환됩니다.
        return new UsernamePasswordAuthenticationToken(principal, "", authorities);
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public String getAdminIdFromToken(String token) {
        return parseClaims(token).getSubject();
    }

    private Claims parseClaims(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token) // 'Jds'가 아닌 'Jws'
                    .getBody();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        }
    }
    // ✅ 'getUserId' 메서드
    public Long getUserId(String token) {
        return Long.parseLong(parseClaims(token).getSubject());
    }

    // ✅ 'getUserType' 메서드
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
