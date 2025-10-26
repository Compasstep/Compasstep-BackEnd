package com.fifo.compasstep.song.controller;

import com.fifo.compasstep.apipayload.ApiResponse;
import com.fifo.compasstep.common.dto.StoreRequestDTO;
import com.fifo.compasstep.common.dto.StoreResponseDTO;
import com.fifo.compasstep.song.repository.SongRepository;
import com.fifo.compasstep.song.service.SongService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/api/song")
@RequiredArgsConstructor
public class SongController {
    private final SongService songService;

    @PostMapping("files/store")
    public ApiResponse<StoreResponseDTO> storeSong(
            @RequestBody StoreRequestDTO request) {
        StoreResponseDTO result = songService.storeSong(request);
        return ApiResponse.success(result);
    }
}
