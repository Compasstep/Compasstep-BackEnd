// src/main/java/com/fifo/compasstep/reference/service/YoutubeLinkService.java
package com.fifo.compasstep.reference.service;

import com.fifo.compasstep.apipayload.exceptions.handler.ReferenceHandler;
import com.fifo.compasstep.reference.client.YoutubeClient;
import com.fifo.compasstep.reference.dto.YoutubeLinkRequestDTO;
import com.fifo.compasstep.reference.dto.YoutubeLinkResponseDTO;
import com.fifo.compasstep.reference.exceptions.ReferenceErrorStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class YoutubeLinkService {

    private final YoutubeClient youtubeClient;

    /** 특정 곡의 유튜브 상위 링크 반환(없으면 404 매핑) */
    @Transactional(readOnly = true)
    public YoutubeLinkResponseDTO getYoutubeLink(YoutubeLinkRequestDTO req) {
        // 1) 입력 검증
        if (req == null) {
            log.warn("[YoutubeLink] null request");
            throw new ReferenceHandler(ReferenceErrorStatus.INVALID_REQUEST);
        }
        final String title = trimToNull(req.title());
        final String artist = trimToNull(req.artist());
        if (title == null) {
            log.warn("[YoutubeLink] missing title. req={}", req);
            throw new ReferenceHandler(ReferenceErrorStatus.TITLE_PARAM_MISSING);
        }
        if (artist == null) {
            log.warn("[YoutubeLink] missing artist. req={}", req);
            throw new ReferenceHandler(ReferenceErrorStatus.ARTIST_PARAM_MISSING);
        }

        try {
            // 2) 외부 호출
            final String url = youtubeClient.findTopVideoUrl(title, artist);

            // 3) 결과 해석: 없으면 404로 매핑
            if (url == null || url.isBlank()) {
                log.info("[YoutubeLink] video not found. title='{}', artist='{}'", title, artist);
                throw new ReferenceHandler(ReferenceErrorStatus.YOUTUBE_VIDEO_NOT_FOUND);
            }
            return new YoutubeLinkResponseDTO(title, artist, url);

        } catch (ReferenceHandler rh) {
            // 도메인 예외는 그대로 전파
            throw rh;

        } catch (Exception e) {
            // 외부 API/파싱 등 예외를 통일 변환
            log.error("[YoutubeLink] external api error. title='{}', artist='{}'", title, artist, e);
            throw new ReferenceHandler(ReferenceErrorStatus.EXTERNAL_API_ERROR);
        }
    }

    private String trimToNull(String s) {
        if (s == null) return null;
        String t = s.trim();
        return t.isEmpty() ? null : t;
    }
}
