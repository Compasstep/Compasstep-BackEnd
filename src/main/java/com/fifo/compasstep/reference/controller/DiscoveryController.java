// src/main/java/com/fifo/compasstep/chat/controller/DiscoveryController.java
package com.fifo.compasstep.reference.controller;

import com.fifo.compasstep.apipayload.ApiResponse;
import com.fifo.compasstep.reference.dto.LyricsAnalysis.LyricsAnalysisResultDTO;
import com.fifo.compasstep.reference.dto.friend.FriendAnalysisResultDTO;
import com.fifo.compasstep.reference.dto.request.DiscoveryKeywordRequestDto;
import com.fifo.compasstep.reference.dto.request.LyricsAnalysisRequestDTO;
import com.fifo.compasstep.reference.dto.request.YoutubeRequestDTO;
import com.fifo.compasstep.reference.dto.response.TrackVideoDto;
import com.fifo.compasstep.reference.dto.youtube.PeerAnalysisResultDTO;
import com.fifo.compasstep.reference.service.DiscoveryService;
import com.fifo.compasstep.security.userDetails.UserUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
public class DiscoveryController {

    private final DiscoveryService service;

    /** 키워드 기반 레퍼런스 탐색 (FastAPI 프록시) */
    @PostMapping("/discovery/keyword")
    @PreAuthorize("hasAuthority('STATUS_NORMAL')")
    public ApiResponse<List<TrackVideoDto>> discoveryKeyword(
            @Valid @RequestBody DiscoveryKeywordRequestDto req,
            @AuthenticationPrincipal UserUserDetails currentUser
    ) {
        Long userId = currentUser.getUser().getId();
        return service.discover(userId, req.query());
    }

    @PostMapping("/analyze/youtube")
    public ApiResponse<PeerAnalysisResultDTO> youtubeAnalyze(
            @AuthenticationPrincipal UserUserDetails currentUser,
            @Valid @RequestBody YoutubeRequestDTO youtubeRequest
            ) {
        Long userId = currentUser.getUser().getId();
        return service.analyzeYoutube(youtubeRequest.getSongId(), youtubeRequest.getArtistName(), userId);
    }

    @PostMapping("/analyze/friend")
    public ApiResponse<FriendAnalysisResultDTO> friendAnalysis(
            @RequestBody FriendAnalysisResultDTO friend
    ) {
        return service.analyzeFriend(friend.getPostId());
    }

    @PostMapping("/analyze/lyrics")
    public ApiResponse<LyricsAnalysisResultDTO> analysisCoaching(
            @RequestBody LyricsAnalysisRequestDTO analysis
    ) {
        return service.analyzeLyrics(analysis.getLyricsId());
    }
}
