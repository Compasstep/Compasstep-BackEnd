package com.fifo.compasstep.security.jwt;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {
    private final String secret;
    private final AccessToken accessToken;
    private final RefreshToken refreshToken;
    private final String algorithm;
    private final String issuer;
    private final String audience;

    public JwtProperties(String secret, AccessToken accessToken, RefreshToken refreshToken, String algorithm, String issuer, String audience) {
        this.secret = secret;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.algorithm = algorithm;
        this.issuer = issuer;
        this.audience = audience;
    }

    @Getter
    public static class AccessToken {
        private final long expiration;

        public AccessToken(long expiration) {
            this.expiration = expiration;
        }
    }

    @Getter
    public static class RefreshToken {
        private final long expiration;
        public RefreshToken(long expiration) {
            this.expiration = expiration;
        }
    }
}
