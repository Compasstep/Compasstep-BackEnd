// src/main/java/com/fifo/compasstep/lyrics_analysis/controller/LyricsAnalysisController.java
package com.fifo.compasstep.lyrics_analysis.controller;

import com.fifo.compasstep.apipayload.ApiResponse;
import com.fifo.compasstep.lyrics_analysis.dto.response.LyricsAnalysisDetailResponseDTO;
import com.fifo.compasstep.lyrics_analysis.dto.response.LyricsAnalysisListResponseDTO;
import com.fifo.compasstep.lyrics_analysis.service.LyricsAnalysisService;
import com.fifo.compasstep.security.userDetails.UserUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user/mypage")
public class LyricsAnalysisController {

    private final LyricsAnalysisService lyricsAnalysisService;

    /** 저장된 가사 분석 목록 조회 */
    @GetMapping("/lyrics-analyses")
    public ApiResponse<LyricsAnalysisListResponseDTO> getAnalyses(
            @AuthenticationPrincipal UserUserDetails currentUser
    ) {
        Long userId = currentUser.getUser().getId();
        var result = lyricsAnalysisService.getList(userId);
        return new ApiResponse<>(200, "저장된 가사 분석 목록 조회가 성공했습니다.", result);
    }

    /** 저장된 가사 분석 상세 조회 */
    @GetMapping("/lyrics-analyses/{analysisId}")
    public ApiResponse<LyricsAnalysisDetailResponseDTO> getAnalysisDetail(
            @PathVariable Long analysisId,
            @AuthenticationPrincipal UserUserDetails currentUser
    ) {
        Long userId = currentUser.getUser().getId();
        var result = lyricsAnalysisService.getDetail(analysisId, userId);
        return new ApiResponse<>(200, "저장된 가사 분석 상세 조회가 성공했습니다.", result);
    }

    /** 저장된 가사 분석 삭제 */
    @DeleteMapping("/lyrics-analyses/{analysisId}")
    public ApiResponse<Void> deleteAnalysis(
            @PathVariable Long analysisId,
            @AuthenticationPrincipal UserUserDetails currentUser
    ) {
        Long userId = currentUser.getUser().getId();
        lyricsAnalysisService.delete(analysisId, userId);
        return new ApiResponse<>(200, "성공했습니다.", null);
    }
}
