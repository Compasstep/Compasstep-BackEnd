// src/main/java/com/fifo/compasstep/reference/service/YoutubeLinkService.java
package com.fifo.compasstep.reference.service;

import com.fifo.compasstep.apipayload.exceptions.GeneralException;
import com.fifo.compasstep.reference.client.YoutubeClient;
import com.fifo.compasstep.reference.dto.YoutubeLinkRequestDTO;
import com.fifo.compasstep.reference.dto.YoutubeLinkResponseDTO;
import com.fifo.compasstep.reference.exceptions.ReferenceErrorStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class YoutubeLinkService {

    private final YoutubeClient youtubeClient;

    /** 특정 곡의 유튜브 상위 링크 반환(없으면 url=null) */
    public YoutubeLinkResponseDTO getYoutubeLink(YoutubeLinkRequestDTO req) {
        try {
            String url = youtubeClient.findTopVideoUrl(req.title(), req.artist());
            return new YoutubeLinkResponseDTO(req.title(), req.artist(), url);
        } catch (Exception e) {
            throw new GeneralException(ReferenceErrorStatus.EXTERNAL_API_ERROR);
        }
    }
}
