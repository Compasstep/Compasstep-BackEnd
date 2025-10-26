package com.fifo.compasstep.lyrics.controller;

import com.fifo.compasstep.apipayload.ApiResponse;
import com.fifo.compasstep.common.dto.StoreRequestDTO;
import com.fifo.compasstep.common.dto.StoreResponseDTO;
import com.fifo.compasstep.lyrics.service.LyricsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/lyrics")
@RequiredArgsConstructor
public class LyricsController {
    private final LyricsService lyricsService;

    @PostMapping("/files/store")
    public ApiResponse<StoreResponseDTO> storeLyrics(
            @RequestBody StoreRequestDTO request) {

        StoreResponseDTO result = lyricsService.storeLyrics(request);
        return ApiResponse.success(result);
    }
}