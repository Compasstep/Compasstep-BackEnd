// src/main/java/com/fifo/compasstep/user/profile/controller/UserProfileController.java
package com.fifo.compasstep.user.profile.controller;

import com.fifo.compasstep.apipayload.ApiResponse;
import com.fifo.compasstep.user.profile.dto.request.UpdateNicknameRequest;
import com.fifo.compasstep.user.profile.dto.request.UpdateProfileImageRequest;
import com.fifo.compasstep.user.profile.dto.response.UserProfileInfoResponseDto;
import com.fifo.compasstep.user.profile.service.UserProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user/profile")
public class UserProfileController {

    private final UserProfileService service;

    // GET /api/user/profile/info
    @GetMapping("/info")
    public ApiResponse<UserProfileInfoResponseDto> getInfo(
            @AuthenticationPrincipal Object principal,
            @RequestHeader(value = "X-DEV-USER-ID", required = false) String devUserId
    ) {
        String effectiveUserId =
                (principal instanceof String s && !"anonymousUser".equalsIgnoreCase(s)) ? (String) principal :
                        (devUserId != null && !devUserId.isBlank()) ? devUserId : "1";
        Long userId = toNumericUserIdOrDefault(effectiveUserId, 1L);

        var result = service.getProfileInfo(userId);
        return new ApiResponse<>(200, "사용자 프로필 정보 조회를 성공했습니다.", result);
    }

    // PATCH /api/user/profile/image
    @PatchMapping("/image")
    public ApiResponse<Void> updateImage(
            @AuthenticationPrincipal Object principal,
            @RequestHeader(value = "X-DEV-USER-ID", required = false) String devUserId,
            @RequestBody @Valid UpdateProfileImageRequest req
    ) {
        String effectiveUserId =
                (principal instanceof String s && !"anonymousUser".equalsIgnoreCase(s)) ? (String) principal :
                        (devUserId != null && !devUserId.isBlank()) ? devUserId : "1";
        Long userId = toNumericUserIdOrDefault(effectiveUserId, 1L);

        service.updateProfileImage(userId, req.getFileKey());
        return new ApiResponse<>(200, "프로필 이미지가 성공적으로 변경되었습니다.", null);
    }

    // PATCH /api/user/profile/nickname
    @PatchMapping("/nickname")
    public ApiResponse<Void> updateNickname(
            @AuthenticationPrincipal Object principal,
            @RequestHeader(value = "X-DEV-USER-ID", required = false) String devUserId,
            @RequestBody @Valid UpdateNicknameRequest req
    ) {
        String effectiveUserId =
                (principal instanceof String s && !"anonymousUser".equalsIgnoreCase(s)) ? (String) principal :
                        (devUserId != null && !devUserId.isBlank()) ? devUserId : "1";
        Long userId = toNumericUserIdOrDefault(effectiveUserId, 1L);

        service.updateNickname(userId, req.getNickname());
        return new ApiResponse<>(200, "닉네임이 성공적으로 변경되었습니다.", null);
    }

    // ===== DEV ONLY: 인증 개발 후 삭제 =====
    private Long toNumericUserIdOrDefault(String effectiveUserId, Long defaultValue) {
        if (effectiveUserId == null || effectiveUserId.isBlank()) return defaultValue;
        try {
            return Long.parseLong(effectiveUserId.trim());
        } catch (NumberFormatException ignored) {
            return defaultValue; // ex) "dev-user-123" → 1L로 대체
        }
    }
}
