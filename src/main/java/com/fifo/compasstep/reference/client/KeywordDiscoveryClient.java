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
}
