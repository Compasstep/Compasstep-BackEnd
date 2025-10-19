package com.fifo.compasstep.reputationAnalysis.controller;

import com.fifo.compasstep.apipayload.ApiResponse;
import com.fifo.compasstep.reputationAnalysis.dto.response.ReputationDetailResponseDto;
import com.fifo.compasstep.reputationAnalysis.dto.response.ReputationListItemDto;
import com.fifo.compasstep.reputationAnalysis.service.ReputationAnalysisService;
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
            // 인증 개발 전: SpEL 제거하고 Object로 받음(익명/문자열 Principal 대응)
            @AuthenticationPrincipal Object principal,
            // @RequestHeader는 인증 개발 완료 후 삭제
            @RequestHeader(value = "X-DEV-USER-ID", required = false) String devUserId
    ) {
        // DEV ONLY: 문자열 기준 Fallback (인증 개발 후 삭제)
        String userIdFromAuth = extractUsername(principal); // ← 인증 붙으면 이 라인만 사용
        String effectiveUserId =
                (userIdFromAuth != null && !userIdFromAuth.isBlank()) ? userIdFromAuth :
                        (devUserId != null && !devUserId.isBlank()) ? devUserId :
                                "1"; // 임시 기본값

        // Long userIdForQuery = Long.valueOf(userIdFromAuth); // 인증 개발 후 적용
        Long userIdForQuery = toNumericUserIdOrDefault(effectiveUserId, 1L); // 인증 개발 후 삭제
        var res = service.getList(userIdForQuery);
        return new ApiResponse<>(200, "저장된 평판 분석 목록 조회를 성공했습니다.", res);
    }

    /** 평판 분석 상세 조회 */
    @GetMapping("/reputation-history/{historyId}")
    public ApiResponse<ReputationDetailResponseDto> getHistoryDetail(
            @PathVariable Long historyId,
            @AuthenticationPrincipal Object principal,
            @RequestHeader(value = "X-DEV-USER-ID", required = false) String devUserId
    ) {
        String userIdFromAuth = extractUsername(principal);
        String effectiveUserId =
                (userIdFromAuth != null && !userIdFromAuth.isBlank()) ? userIdFromAuth :
                        (devUserId != null && !devUserId.isBlank()) ? devUserId :
                                "1";
        Long userIdForQuery = toNumericUserIdOrDefault(effectiveUserId, 1L);

        var res = service.getDetail(historyId, userIdForQuery);
        return new ApiResponse<>(200, "평판 분석 상세 조회를 성공했습니다.", res);
    }

    /** 저장된 평판 분석 삭제 */
    @DeleteMapping("/reputation-history/{historyId}")
    public ApiResponse<Void> deleteHistory(
            @PathVariable Long historyId,
            @AuthenticationPrincipal Object principal,
            @RequestHeader(value = "X-DEV-USER-ID", required = false) String devUserId
    ) {
        String userIdFromAuth = extractUsername(principal);
        String effectiveUserId =
                (userIdFromAuth != null && !userIdFromAuth.isBlank()) ? userIdFromAuth :
                        (devUserId != null && !devUserId.isBlank()) ? devUserId :
                                "1";
        Long userIdForQuery = toNumericUserIdOrDefault(effectiveUserId, 1L);

        service.delete(historyId, userIdForQuery);
        return new ApiResponse<>(200, "성공했습니다", null);
    }

    // 아래 함수들도 인증 기능 개발 후 삭제
    private String extractUsername(Object principal) {
        if (principal == null) return null;
        if (principal instanceof org.springframework.security.core.userdetails.UserDetails u) {
            return u.getUsername();
        }
        if (principal instanceof String s) { // "anonymousUser"
            return !"anonymousUser".equalsIgnoreCase(s) ? s : null;
        }
        try {
            var m = principal.getClass().getMethod("getUsername");
            Object v = m.invoke(principal);
            return (v instanceof String str && !str.isBlank()) ? str : null;
        } catch (Exception ignore) {
            return null;
        }
    }
    private Long toNumericUserIdOrDefault(String effectiveUserId, Long defaultValue) {
        if (effectiveUserId == null || effectiveUserId.isBlank()) return defaultValue;
        try {
            return Long.parseLong(effectiveUserId.trim());
        } catch (NumberFormatException ignored) {
            return defaultValue; // ex) "dev-user-123" → 1L 로 대체
        }
    }
}
