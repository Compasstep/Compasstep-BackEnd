package com.fifo.compasstep.config;

import java.time.Duration;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

@Configuration
@EnableConfigurationProperties(ExternalApiProperties.class)
@RequiredArgsConstructor
public class ExternalApiConfig {

    private final ExternalApiProperties props;

    @Bean("spotifyAuthClient")
    public RestClient spotifyAuthClient() {
        var cfg = props.getSpotify();
        return RestClient.builder()
                .baseUrl(cfg.getAuthUrl())
                .requestFactory(simpleRequestFactory(cfg.getTimeoutMs()))
                .build();
    }

    @Bean("spotifyApiClient")
    public RestClient spotifyApiClient() {
        var cfg = props.getSpotify();
        return RestClient.builder()
                .baseUrl(cfg.getApiUrl())
                .requestFactory(simpleRequestFactory(cfg.getTimeoutMs()))
                .build();
    }

    @Bean("youtubeRestClient")
    @Lazy
    public RestClient youtubeRestClient() {
        var cfg = props.getYoutube();
        return RestClient.builder()
                .baseUrl(cfg.getBaseUrl())
                .requestFactory(simpleRequestFactory(cfg.getTimeoutMs()))
                .build();
    }

    private ClientHttpRequestFactory simpleRequestFactory(long timeoutMs) {
        var f = new SimpleClientHttpRequestFactory();
        f.setConnectTimeout(Duration.ofMillis(timeoutMs));
        f.setReadTimeout(Duration.ofMillis(timeoutMs));
        return f;
    }
}
