package com.fifo.compasstep.reference.client;

import com.fifo.compasstep.config.ExternalApiProperties;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class YoutubeClient {

    private final WebClient youtubeWebClient;   // @Bean("youtubeWebClient")
    private final ExternalApiProperties props;  // apiKey 등 설정

    public YoutubeClient(@Qualifier("youtubeWebClient") WebClient youtubeWebClient,
                         ExternalApiProperties props) {
        this.youtubeWebClient = youtubeWebClient;
        this.props = props;
    }

    /** 제목+아티스트로 검색해서 최상위 1건의 YouTube Watch URL 반환. 없으면 null */
    @SuppressWarnings("unchecked")
    public String findTopVideoUrl(String title, String artist) {
        var cfg = props.getYoutube();
        String q = (artist == null || artist.isBlank()) ? title : (artist + " - " + title);

        Map<String, Object> res = youtubeWebClient.get()
                .uri(uri -> uri.path("/search")
                        .queryParam("part", "snippet")
                        .queryParam("type", "video")
                        .queryParam("maxResults", 1)
                        .queryParam("q", q)
                        .queryParam("key", cfg.getApiKey())
                        .build())
                .retrieve()
                .bodyToMono(Map.class)
                .block();

        if (res == null) return null;

        List<Map<String, Object>> items =
                (List<Map<String, Object>>) res.getOrDefault("items", List.of());
        if (items.isEmpty()) return null;

        Map<String, Object> first = items.get(0);
        Map<String, Object> id = (Map<String, Object>) first.getOrDefault("id", Map.of());
        String videoId = Objects.toString(id.get("videoId"), "");
        if (videoId.isBlank()) return null;

        return "https://www.youtube.com/watch?v=" + videoId;
    }
}
