// src/main/java/com/fifo/compasstep/chat/controller/DiscoveryController.java
package com.fifo.compasstep.chat.controller;

import com.fifo.compasstep.apipayload.ApiResponse;
import com.fifo.compasstep.chat.dto.request.DiscoveryKeywordRequestDto;
import com.fifo.compasstep.chat.dto.response.TrackVideoDto;
import com.fifo.compasstep.chat.service.DiscoveryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user/mypage")
public class DiscoveryController {

    private final DiscoveryService service;

    /** 키워드 기반 레퍼런스 탐색 (FastAPI 프록시) */
    @PostMapping("/discovery/keyword")
    public ApiResponse<List<TrackVideoDto>> discoveryKeyword(
            @Valid @RequestBody DiscoveryKeywordRequestDto req,
            // ✅ 인증 전: SpEL 제거(Object로 받음)
            @AuthenticationPrincipal Object principal,
            // ✅ DEV ONLY: 임시 헤더(실서비스 전 제거)
            @RequestHeader(value = "X-DEV-USER-ID", required = false) String devUserId
    ) {
        // DEV ONLY Fallback (인증 붙으면 제거)
        String userIdFromAuth = extractUsername(principal); // 인증 후엔 이 값만 사용
        String effectiveUserId =
                (userIdFromAuth != null && !userIdFromAuth.isBlank()) ? userIdFromAuth :
                        (devUserId != null && !devUserId.isBlank()) ? devUserId :
                                "1"; // 숫자 문자열 기본값

        // Long userIdForQuery = Long.parseLong(userIdFromAuth); // 인증 후 적용
        Long userIdForQuery = toNumericUserIdOrDefault(effectiveUserId, 1L); // 인증 후 삭제

        return service.discover(userIdForQuery, req.query());
    }

    /* ====================== DEV ONLY helpers ======================
       - 문자열 Fallback + 숫자 변환 기본값
       - TODO: 인증 붙으면 제거하고 @AuthenticationPrincipal Long userId 로 단순화
    ================================================================= */
    private String extractUsername(Object principal) {
        if (principal == null) return null;
        if (principal instanceof org.springframework.security.core.userdetails.UserDetails u) return u.getUsername();
        if (principal instanceof String s) return !"anonymousUser".equalsIgnoreCase(s) ? s : null;
        try {
            var m = principal.getClass().getMethod("getUsername");
            Object v = m.invoke(principal);
            return (v instanceof String str && !str.isBlank()) ? str : null;
        } catch (Exception ignore) { return null; }
    }

    private Long toNumericUserIdOrDefault(String effectiveUserId, Long defaultValue) {
        if (effectiveUserId == null || effectiveUserId.isBlank()) return defaultValue;
        try { return Long.parseLong(effectiveUserId.trim()); }
        catch (NumberFormatException ignored) { return defaultValue; }
    }
}
