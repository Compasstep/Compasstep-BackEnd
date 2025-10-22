// src/main/java/com/fifo/compasstep/reference/service/YoutubeLinkService.java
package com.fifo.compasstep.reference.service;

import com.fifo.compasstep.apipayload.exceptions.GeneralException;
import com.fifo.compasstep.reference.client.YoutubeClient;
import com.fifo.compasstep.reference.dto.YoutubeLinkRequestDto;
import com.fifo.compasstep.reference.dto.YoutubeLinkResponseDto;
import com.fifo.compasstep.reference.exceptions.ReferenceErrorStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class YoutubeLinkService {

    private final YoutubeClient youtubeClient;

    /** 특정 곡의 유튜브 상위 링크 반환(없으면 404 매핑) */
    @Transactional(readOnly = true)
    public YoutubeLinkResponseDto getYoutubeLink(YoutubeLinkRequestDto req) {
        // 1) 입력 검증
        if (req == null) {
            throw new GeneralException(ReferenceErrorStatus.INVALID_REQUEST);
        }
        final String title = trimToNull(req.title());
        final String artist = trimToNull(req.artist());
        if (title == null) {
            throw new GeneralException(ReferenceErrorStatus.TITLE_PARAM_MISSING);
        }
        if (artist == null) {
            throw new GeneralException(ReferenceErrorStatus.ARTIST_PARAM_MISSING);
        }

        try {
            // 2) 외부 호출
            final String url = youtubeClient.findTopVideoUrl(title, artist);

            // 3) 결과 해석: 없으면 404로 매핑
            if (url == null || url.isBlank()) {
                throw new GeneralException(ReferenceErrorStatus.YOUTUBE_VIDEO_NOT_FOUND);
            }
            return new YoutubeLinkResponseDto(title, artist, url);

        } catch (GeneralException ge) {
            throw ge;
        } catch (Exception e) {
            throw new GeneralException(ReferenceErrorStatus.EXTERNAL_API_ERROR);
        }
    }

    private String trimToNull(String s) {
        if (s == null) return null;
        String t = s.trim();
        return t.isEmpty() ? null : t;
    }
}
