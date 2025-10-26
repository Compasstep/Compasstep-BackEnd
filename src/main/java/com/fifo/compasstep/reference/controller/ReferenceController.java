// src/main/java/com/fifo/compasstep/reference/controller/ReferenceController.java
package com.fifo.compasstep.reference.controller;

import com.fifo.compasstep.apipayload.ApiResponse;
import com.fifo.compasstep.reference.dto.RankingItemDTO;
import com.fifo.compasstep.reference.dto.ReferenceRequestDTO;
import com.fifo.compasstep.reference.dto.YoutubeLinkRequestDTO;
import com.fifo.compasstep.reference.dto.YoutubeLinkResponseDTO;
import com.fifo.compasstep.reference.service.GenreRankingService;
import com.fifo.compasstep.reference.service.YoutubeLinkService;
import com.fifo.compasstep.security.userDetails.UserUserDetails;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
public class ReferenceController {

    private final GenreRankingService genreRankingService;
    private final YoutubeLinkService youtubeLinkService;

    @GetMapping("/rankings")
    public ApiResponse<List<RankingItemDTO>> getRankings(
            @AuthenticationPrincipal UserUserDetails currentUser,
            @RequestParam @NotBlank(message = "장르를 지정해주세요.") String genre,
            @RequestParam(required = false) String market,
            @RequestParam(required = false) @Min(1) @Max(50) Integer limit
    ) {
        List<RankingItemDTO> result = genreRankingService.getGenreRanking(
                new ReferenceRequestDTO(genre, market, limit)
        );
        return ApiResponse.success(result);
    }

    @GetMapping("/youtube-link")
    public ApiResponse<YoutubeLinkResponseDTO> getYoutubeLink(
            @AuthenticationPrincipal UserUserDetails currentUser,
            @RequestParam @NotBlank(message = "곡 제목을 지정해주세요.") String title,
            @RequestParam @NotBlank(message = "아티스트 이름을 지정해주세요.") String artist
    ) {
        YoutubeLinkResponseDTO result = youtubeLinkService.getYoutubeLink(
                new YoutubeLinkRequestDTO(title, artist)
        );
        return ApiResponse.success(result);
    }
}
