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

    @Bean("fastApiClient")
    @Lazy
    public WebClient fastApiClient() {

        var cfg = props.getFastapi();
        int baseTimeout = cfg.getTimeoutMs(); // 기존 5000 (5초)

        HttpClient httpClient = HttpClient.create()
                .option(io.netty.channel.ChannelOption.CONNECT_TIMEOUT_MILLIS, baseTimeout)
                .responseTimeout(Duration.ofMillis(baseTimeout * 6))  // 🔥 최소 30초 확보
                .doOnConnected(conn -> conn
                        .addHandlerLast(new io.netty.handler.timeout.ReadTimeoutHandler(baseTimeout * 4, java.util.concurrent.TimeUnit.MILLISECONDS))
                        .addHandlerLast(new io.netty.handler.timeout.WriteTimeoutHandler(baseTimeout, java.util.concurrent.TimeUnit.MILLISECONDS))
                );

        return WebClient.builder()
                .baseUrl(cfg.getBaseUrl())
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .defaultHeader("Content-Type", "application/json")
                .build();
    }


    // Prometheus 클라이언트 추가
    @Bean("prometheusClient")
    public WebClient prometheusClient() {
        var cfg = props.getPrometheus();
        return WebClient.builder()
                .baseUrl(cfg.getBaseUrl())  // base-url 설정
                .clientConnector(new ReactorClientHttpConnector(
                        HttpClient.create().responseTimeout(Duration.ofMillis(cfg.getTimeoutMs()))
                ))
                .build();
    }
}
