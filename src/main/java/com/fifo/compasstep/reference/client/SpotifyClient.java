package com.fifo.compasstep.reference.client;

import com.fifo.compasstep.config.ExternalApiProperties;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class SpotifyClient {

    private final WebClient spotifyAuthClient;  // @Bean("spotifyAuthClient")
    private final WebClient spotifyApiClient;   // @Bean("spotifyApiClient")
    private final ExternalApiProperties props;

    /** Client Credentials Flow로 액세스 토큰 발급 */
    @SuppressWarnings("unchecked")
    public String getAccessToken() {
        var cfg = props.getSpotify();
        String basic = Base64.getEncoder().encodeToString(
                (cfg.getClientId() + ":" + cfg.getClientSecret()).getBytes(StandardCharsets.UTF_8)
        );

        var form = new LinkedMultiValueMap<String, String>();
        form.add("grant_type", "client_credentials");

        Map<String, Object> tokenJson = spotifyAuthClient.post()
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .header(HttpHeaders.AUTHORIZATION, "Basic " + basic)
                .bodyValue(form)
                .retrieve()
                .onStatus(HttpStatusCode::isError, r ->
                        r.bodyToMono(String.class).flatMap(b -> Mono.error(new IllegalStateException("Spotify auth error: " + b)))
                )
                .bodyToMono(Map.class)
                .block();

        Object access = tokenJson.get("access_token");
        if (access == null) throw new IllegalStateException("Spotify token not found");
        return access.toString();
    }

    /**
     * 장르 기반 트랙 검색. limit<=50, offset 지원.
     * 응답은 Spotify /search 그대로(Map) 반환.
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object> searchTracksByGenre(String genre, String market, int limit, int offset, String bearer) {
        return spotifyApiClient.get()
                .uri(uri -> uri.path("/search")
                        .queryParam("q", "genre:\"" + genre + "\"")
                        .queryParam("type", "track")
                        .queryParam("market", market)
                        .queryParam("limit", Math.min(Math.max(limit, 1), 50))
                        .queryParam("offset", Math.max(offset, 0))
                        .build())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + bearer)
                .retrieve()
                .onStatus(HttpStatusCode::isError, r ->
                        r.bodyToMono(String.class).flatMap(b -> Mono.error(new IllegalStateException("Spotify search error: " + b)))
                )
                .bodyToMono(Map.class)
                .block();
    }
}
