package com.fifo.compasstep.reference.client;

import com.fifo.compasstep.config.ExternalApiProperties;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestClient;

@Component
@RequiredArgsConstructor
public class SpotifyClient {

    // ExternalApiConfig에서 등록한 RestClient 빈(@Bean("spotifyAuthClient"), @Bean("spotifyApiClient"))을 주입
    private final RestClient spotifyAuthClient;
    private final RestClient spotifyApiClient;
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

        ResponseEntity<Map> entity = spotifyAuthClient.post()
                .uri("") // baseUrl로 POST
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .header(HttpHeaders.AUTHORIZATION, "Basic " + basic)
                .body(form)
                .retrieve()
                .toEntity(Map.class);

        if (!entity.getStatusCode().is2xxSuccessful()) {
            throw new IllegalStateException("Spotify auth error: " + entity.getStatusCode());
        }
        Map<String, Object> tokenJson = entity.getBody();
        if (tokenJson == null || tokenJson.get("access_token") == null) {
            throw new IllegalStateException("Spotify token not found");
        }
        return tokenJson.get("access_token").toString();
    }

    /**
     * 장르 기반 트랙 검색. limit<=50, offset 지원.
     * 응답은 Spotify /search 그대로(Map) 반환.
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object> searchTracksByGenre(String genre, String market, int limit, int offset, String bearer) {
        int safeLimit = Math.min(Math.max(limit, 1), 50);
        int safeOffset = Math.max(offset, 0);

        ResponseEntity<Map> entity = spotifyApiClient.get()
                .uri(uri -> uri.path("/search")
                        .queryParam("q", "genre:\"" + genre + "\"")
                        .queryParam("type", "track")
                        .queryParam("market", market)
                        .queryParam("limit", safeLimit)
                        .queryParam("offset", safeOffset)
                        .build())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + bearer)
                .retrieve()
                .toEntity(Map.class);

        if (!entity.getStatusCode().is2xxSuccessful()) {
            throw new IllegalStateException("Spotify search error: " + entity.getStatusCode());
        }
        return entity.getBody();
    }
}
