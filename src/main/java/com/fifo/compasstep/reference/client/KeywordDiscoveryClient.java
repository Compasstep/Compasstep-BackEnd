// src/main/java/com/fifo/compasstep/chat/client/KeywordDiscoveryClient.java
package com.fifo.compasstep.reference.client;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class KeywordDiscoveryClient {

    private final WebClient fastApiClient; // @Qualifier("fastApiClient")로 주입받는 구성이라면 생성자에 붙이기

    @SuppressWarnings("unchecked")
    public Map<String, Object> discoveryByKeyword(Long userId, String query) {
        Map<String, Object> payload = Map.of(
                "user_id", userId,
                "query", query
        );

        return fastApiClient.post()
                .uri("/ai/user/discovery/keyword")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(payload)
                // 핵심: 상태와 무관하게 바디를 읽고 그대로 돌려준다
                .exchangeToMono(resp ->
                        resp.bodyToMono(Map.class)
                                .defaultIfEmpty(new HashMap<>())
                                .map(body -> {
                                    // FastAPI가 code를 넣어주지만, 혹시 없으면 HTTP status로 채움
                                    body.putIfAbsent("code", String.valueOf(resp.statusCode().value()));
                                    body.putIfAbsent("message", "");
                                    body.putIfAbsent("result", null);
                                    return body;
                                })
                )
                .block();
    }
    public Map<String, Object> analyzePeerReputation(Long songId, String artistName, Long userId) { // 예시 파라미터
        Map<String, Object> payload = Map.of(
                "song_id", songId,
                "artist_name", artistName,
                "user_id", userId// FastAPI가 받을 파라미터
        );

        return fastApiClient.post()
                .uri("/ai/user/analyze/peer") // 새 API 엔드포인트
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(payload)
                // exchangeToMono 로직은 기존과 동일하게 사용
                .exchangeToMono(resp ->
                        resp.bodyToMono(Map.class)
                                .defaultIfEmpty(new HashMap<>())
                                .map(body -> {
                                    body.putIfAbsent("code", String.valueOf(resp.statusCode().value()));
                                    body.putIfAbsent("message", "");
                                    body.putIfAbsent("result", null);
                                    return body;
                                })
                )
                .block();
    }

    public Map<String, Object> analyzeFriendReputation(Long postId) {
        // Python으로 전달할 페이로드. Python이 'post_id'를 받을 것으로 예상
        Map<String, Object> payload = Map.of(
                "post_id", postId
        );

        // API 엔드포인트는 FastAPI와 협의된 경로를 사용해야 합니다.
        // 예: /ai/user/analyze/friend
        return fastApiClient.post()
                .uri("/ai/user/analyze/friend") // Python FastAPI 엔드포인트
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(payload)
                .exchangeToMono(resp ->
                        resp.bodyToMono(Map.class)
                                .defaultIfEmpty(new HashMap<>())
                                .map(body -> {
                                    body.putIfAbsent("code", String.valueOf(resp.statusCode().value()));
                                    body.putIfAbsent("message", "");
                                    body.putIfAbsent("result", null);
                                    return body;
                                })
                )
                .block();
    }

    // KeywordDiscoveryClient.java 클래스 내에 추가

    /**
     * lyrics_id를 FastAPI로 전송하고 가사 분석 및 코칭 결과를 받습니다.
     * @param lyricsId 프론트엔드에서 받은 가사 ID
     * @return FastAPI로부터 받은 응답 Map
     */
    public Map<String, Object> analyzeLyrics(Long lyricsId) {
        // Python으로 전달할 페이로드. Python이 'lyrics_id'를 받을 것으로 예상
        Map<String, Object> payload = Map.of(
                "lyrics_id", lyricsId
        );

        // Python FastAPI 엔드포인트
        return fastApiClient.post()
                .uri("/ai/user/analyze/lyrics")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(payload)
                .exchangeToMono(resp ->
                        resp.bodyToMono(Map.class)
                                .defaultIfEmpty(new HashMap<>())
                                .map(body -> {
                                    body.putIfAbsent("code", String.valueOf(resp.statusCode().value()));
                                    body.putIfAbsent("message", "");
                                    body.putIfAbsent("result", null);
                                    return body;
                                })
                )
                .block();
    }
}
