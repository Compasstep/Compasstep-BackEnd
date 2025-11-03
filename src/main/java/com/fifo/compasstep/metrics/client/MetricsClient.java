package com.fifo.compasstep.metrics.client;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class MetricsClient {

    // Prometheus 클라이언트를 주입받음
    @Qualifier("prometheusClient")
    private final WebClient prometheusClient;

    // Prometheus 쿼리 실행 메서드
    public Map<String, Object> queryMetrics(String query) {
        return prometheusClient.get()
                .uri(uri -> uri.path("/api/v1/query")
                        .queryParam("query", query)
                        .build())
                .retrieve()
                .bodyToMono(Map.class)  // Map으로 응답을 받도록 수정
                .block();
    }
}
