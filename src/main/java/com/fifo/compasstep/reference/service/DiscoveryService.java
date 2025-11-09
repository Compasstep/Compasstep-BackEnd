// src/main/java/com/fifo/compasstep/chat/service/DiscoveryService.java
package com.fifo.compasstep.reference.service;

import com.fifo.compasstep.apipayload.ApiResponse;
import com.fifo.compasstep.apipayload.exceptions.handler.ReferenceHandler; // ✅ 변경
import com.fifo.compasstep.reference.client.KeywordDiscoveryClient;
import com.fifo.compasstep.reference.dto.response.TrackVideoDto;
import com.fifo.compasstep.reference.exceptions.DiscoveryErrorStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class DiscoveryService {

    private final KeywordDiscoveryClient client;

    /** FastAPI로 전달 후 우리 ApiResponse로 변환 */
    @SuppressWarnings("unchecked")
    public ApiResponse<List<TrackVideoDto>> discover(Long userId, String query) {
        Map<String, Object> res;
        try {
            res = client.discoveryByKeyword(userId, query);
        } catch (Exception e) {
            throw new ReferenceHandler(DiscoveryErrorStatus.FASTAPI_ERROR); // ✅ 변경
        }
        if (res == null) throw new ReferenceHandler(DiscoveryErrorStatus.FASTAPI_ERROR); // ✅ 변경

        String code = Objects.toString(res.get("code"), "");
        String message = Objects.toString(res.get("message"), "");
        Object resultObj = res.get("result");

        switch (code) {
            case "200" -> {
                List<TrackVideoDto> items = toTrackVideoList(resultObj);
                return new ApiResponse<>(200,
                        (message != null && !message.isBlank()) ? message : "성공",
                        items);
            }
            case "204" -> {
                return new ApiResponse<>(204,
                        (message != null && !message.isBlank()) ? message : "추천 결과가 없습니다.",
                        List.of());
            }
            case "422" -> throw new ReferenceHandler(DiscoveryErrorStatus.FASTAPI_UNPROCESSABLE);
            case "403" -> throw new ReferenceHandler(DiscoveryErrorStatus.USER_FORBIDDEN);
            case "404" -> throw new ReferenceHandler(DiscoveryErrorStatus.USER_NOT_FOUND);
            case "400" -> throw new ReferenceHandler(DiscoveryErrorStatus.INVALID_REQUEST);
            default -> throw new ReferenceHandler(DiscoveryErrorStatus.FASTAPI_ERROR);
        }
    }

    @SuppressWarnings("unchecked")
    private List<TrackVideoDto> toTrackVideoList(Object resultObj) {
        if (!(resultObj instanceof List<?> raw)) return List.of();
        List<TrackVideoDto> out = new ArrayList<>();
        for (Object o : raw) {
            if (o instanceof Map<?,?> m) {
                out.add(TrackVideoDto.builder()
                        .videoId(Objects.toString(m.get("videoId"), ""))
                        .title(Objects.toString(m.get("title"), ""))
                        .channelName(Objects.toString(m.get("channelName"), ""))
                        .thumbnailUrl(Objects.toString(m.get("thumbnailUrl"), ""))
                        .youtubeUrl(Objects.toString(m.get("youtubeUrl"), ""))
                        .build());
            } else if (o instanceof String s) {
                out.add(TrackVideoDto.builder()
                        .videoId("").title(s).channelName("")
                        .thumbnailUrl("").youtubeUrl("").build());
            }
        }
        return out;
    }
}
