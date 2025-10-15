// src/main/java/com/fifo/compasstep/lyrics_analysis/controller/LyricsAnalysisController.java
package com.fifo.compasstep.lyrics_analysis.controller;

import com.fifo.compasstep.apipayload.ApiResponse;
import com.fifo.compasstep.lyrics_analysis.dto.response.LyricsAnalysisDetailResponseDTO;
import com.fifo.compasstep.lyrics_analysis.dto.response.LyricsAnalysisListResponseDTO;
import com.fifo.compasstep.lyrics_analysis.service.LyricsAnalysisService;
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
            @AuthenticationPrincipal Object principal, // 인증 개발 후 변경
            // @RequestHeader는 인증 개발 완료 후 삭제
            @RequestHeader(value = "X-DEV-USER-ID", required = false) String devUserId
    ) {
        // 문자열 기준 Fallback 아래 내용은 인증 개발 후 삭제
        String userIdFromAuth = extractUsername(principal); // ← 인증 붙으면 이 라인만 사용
        String effectiveUserId =
                (userIdFromAuth != null && !userIdFromAuth.isBlank()) ? userIdFromAuth :
                        (devUserId != null && !devUserId.isBlank()) ? devUserId :
                                "dev-user-123"; // 임시 기본값

        // Long userIdForQuery = Long.parseLong(userIdFromAuth); // 인증 개발 후 변경하여 적용
        Long userIdForQuery = toNumericUserIdOrDefault(effectiveUserId, 1L); // 인증 개발 후 삭제
        var result = lyricsAnalysisService.getList(userIdForQuery);
        return new ApiResponse<>(200, "저장된 가사 분석 목록 조회가 성공했습니다.", result);
    }

    /** 저장된 가사 분석 상세 조회 */
    @GetMapping("/lyrics-analyses/{analysisId}")
    public ApiResponse<LyricsAnalysisDetailResponseDTO> getAnalysisDetail(
            @PathVariable Long analysisId,
            @AuthenticationPrincipal Object principal,
            // @RequestHeader는 인증 개발 완료 후 삭제
            @RequestHeader(value = "X-DEV-USER-ID", required = false) String devUserId
    ) {
        // 아래 내용은 인증 개발 후 삭제
        String userIdFromAuth = extractUsername(principal);
        String effectiveUserId =
                (userIdFromAuth != null && !userIdFromAuth.isBlank()) ? userIdFromAuth :
                        (devUserId != null && !devUserId.isBlank()) ? devUserId :
                                "dev-user-123"; // 임시 기본값

        // Long userIdForQuery = Long.parseLong(userIdFromAuth); // // 인증 개발 후 변경하여 적용
        Long userIdForQuery = toNumericUserIdOrDefault(effectiveUserId, 1L); // 인증 개발 후 삭제
        var result = lyricsAnalysisService.getDetail(analysisId, userIdForQuery);
        return new ApiResponse<>(200, "저장된 가사 분석 상세 조회가 성공했습니다.", result);
    }

    /** 저장된 가사 분석 삭제 */
    @DeleteMapping("/lyrics-analyses/{analysisId}")
    public ApiResponse<Void> deleteAnalysis(
            @PathVariable Long analysisId,
            @AuthenticationPrincipal Object principal,
            // @RequestHeader는 인증 개발 완료 후 삭제
            @RequestHeader(value = "X-DEV-USER-ID", required = false) String devUserId
    ) {
        // 아래 내용은 인증 개발 후 삭제
        String userIdFromAuth = extractUsername(principal);
        String effectiveUserId =
                (userIdFromAuth != null && !userIdFromAuth.isBlank()) ? userIdFromAuth :
                        (devUserId != null && !devUserId.isBlank()) ? devUserId :
                                "dev-user-123"; // 임시 기본값

        // Long userIdForQuery = Long.parseLong(userIdFromAuth); // 인증 개발 후 변경하여 적용
        Long userIdForQuery = toNumericUserIdOrDefault(effectiveUserId, 1L);// 인증 개발 후 변경하여 적용
        lyricsAnalysisService.delete(analysisId, userIdForQuery);
        return new ApiResponse<>(200, "성공했습니다.", null);
    }


    // 아래 내용 부터는 인증 개발 되면 삭제
    // 인증 붙기 전까지 다양한 Principal 타입을 안전하게 username으로 추출
    // 인증 개발 후에는 컨트롤러 파라미터를 @AuthenticationPrincipal Long userId 형태로 바꿔 이 메서드 삭제
    private String extractUsername(Object principal) {
        if (principal == null) return null;
        // Spring Security 기본 UserDetails
        if (principal instanceof org.springframework.security.core.userdetails.UserDetails u) {
            return u.getUsername();
        }
        // 문자열 Principal (예: "anonymousUser")
        if (principal instanceof String s) {
            return !"anonymousUser".equalsIgnoreCase(s) ? s : null;
        }
        // 커스텀 Principal (getUsername() 메서드가 있을 경우)
        try {
            var m = principal.getClass().getMethod("getUsername");
            Object v = m.invoke(principal);
            return (v instanceof String str && !str.isBlank()) ? str : null;
        } catch (Exception ignore) {
            return null;
        }
    }

    // 아래도 인증 개발 후 삭제
    private Long toNumericUserIdOrDefault(String effectiveUserId, Long defaultValue) {
        if (effectiveUserId == null || effectiveUserId.isBlank()) return defaultValue;
        try {
            return Long.parseLong(effectiveUserId.trim());
        } catch (NumberFormatException ignored) {
            return defaultValue; // ex) "dev-user-123" → 1L 로 대체
        }
    }
}
