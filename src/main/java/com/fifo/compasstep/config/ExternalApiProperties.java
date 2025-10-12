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

    private Spotify spotify;
    private Youtube youtube;
}

