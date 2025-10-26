package com.fifo.compasstep.reputationAnalysis.controller;

import com.fifo.compasstep.apipayload.ApiResponse;
import com.fifo.compasstep.reputationAnalysis.dto.response.ReputationDetailResponseDto;
import com.fifo.compasstep.reputationAnalysis.dto.response.ReputationListItemDto;
import com.fifo.compasstep.reputationAnalysis.service.ReputationAnalysisService;
import com.fifo.compasstep.security.userDetails.UserUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user/mypage")
public class ReputationAnalysisController {

    private final ReputationAnalysisService service;

    /** 저장된 평판 분석 목록 조회 */
    @GetMapping("/reputation-history")
    public ApiResponse<List<ReputationListItemDto>> getHistoryList(
            @AuthenticationPrincipal UserUserDetails currentUser
    ) {
        Long userId = currentUser.getUser().getId();
        var result = service.getList(userId);
        return ApiResponse.success(result);
    }

    /** 평판 분석 상세 조회 */
    @GetMapping("/reputation-history/{historyId}")
    public ApiResponse<ReputationDetailResponseDto> getHistoryDetail(
            @PathVariable Long historyId,
            @AuthenticationPrincipal UserUserDetails currentUser
    ) {
        Long userId = currentUser.getUser().getId();
        var result = service.getDetail(historyId, userId);
        return ApiResponse.success(result);
    }

    /** 저장된 평판 분석 삭제 */
    @DeleteMapping("/reputation-history/{historyId}")
    public ApiResponse<Void> deleteHistory(
            @PathVariable Long historyId,
            @AuthenticationPrincipal UserUserDetails currentUser
    ) {
        Long userId = currentUser.getUser().getId();
        service.delete(historyId, userId);
        return ApiResponse.success(null);
    }
}
