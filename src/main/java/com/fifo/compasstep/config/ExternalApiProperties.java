package com.fifo.compasstep.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Setter
@Getter
@ConfigurationProperties(prefix = "external-apis")
public class ExternalApiProperties {

    @Setter
    @Getter
    public static class Spotify {
        private String authUrl;
        private String apiUrl;
        private String clientId;
        private String clientSecret;
        private Integer timeoutMs = 5000;
    }

    @Setter
    @Getter
    public static class Youtube {
        private String baseUrl;
        private String apiKey;
        private Integer timeoutMs = 5000;
    }

    @Getter
    @Setter
    public static class Fastapi {
        private String baseUrl;
        private int timeoutMs = 500000;
    }

    // Prometheus 관련 설정 추가
    @Getter
    @Setter
    public static class Prometheus {
        private String baseUrl;  // Prometheus의 base URL
        private int timeoutMs = 5000;  // 타임아웃 설정
    }

    private Spotify spotify;
    private Youtube youtube;
    private Fastapi fastapi;
    private Prometheus prometheus;  // Prometheus 설정 객체
}
