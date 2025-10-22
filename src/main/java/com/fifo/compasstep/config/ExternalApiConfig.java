package com.fifo.compasstep.config;

import java.time.Duration;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.*;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

@Configuration
@EnableConfigurationProperties(ExternalApiProperties.class)
@RequiredArgsConstructor
public class ExternalApiConfig {

    private final ExternalApiProperties props;

    @Bean("spotifyAuthClient")
    public WebClient spotifyAuthClient() {
        var cfg = props.getSpotify();
        return WebClient.builder()
                .baseUrl(cfg.getAuthUrl())
                .clientConnector(new ReactorClientHttpConnector(
                        HttpClient.create().responseTimeout(Duration.ofMillis(cfg.getTimeoutMs()))
                ))
                .build();
    }

    @Bean("spotifyApiClient")
    public WebClient spotifyApiClient() {
        var cfg = props.getSpotify();
        return WebClient.builder()
                .baseUrl(cfg.getApiUrl())
                .clientConnector(new ReactorClientHttpConnector(
                        HttpClient.create().responseTimeout(Duration.ofMillis(cfg.getTimeoutMs()))
                ))
                .build();
    }

    @Bean("youtubeWebClient")
    @Lazy
    public WebClient youtubeWebClient() {
        var cfg = props.getYoutube();
        return WebClient.builder()
                .baseUrl(cfg.getBaseUrl())
                .clientConnector(new ReactorClientHttpConnector(
                        HttpClient.create().responseTimeout(Duration.ofMillis(cfg.getTimeoutMs()))
                ))
                .build();
    }
}
