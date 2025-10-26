// src/main/java/com/fifo/compasstep/user/profile/controller/UserProfileController.java
package com.fifo.compasstep.user.profile.controller;

import com.fifo.compasstep.apipayload.ApiResponse;
import com.fifo.compasstep.security.userDetails.UserUserDetails;
import com.fifo.compasstep.user.profile.dto.request.UpdateNicknameRequestDTO;
import com.fifo.compasstep.user.profile.dto.request.UpdateProfileImageRequestDTO;
import com.fifo.compasstep.user.profile.dto.response.UserProfileInfoResponseDTO;
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

    @GetMapping("/info")
    public ApiResponse<UserProfileInfoResponseDTO> getInfo(
            @AuthenticationPrincipal UserUserDetails currentUser
    ) {
        Long userId = currentUser.getUser().getId();
        var result = service.getProfileInfo(userId);
        return new ApiResponse<>(200, "사용자 프로필 정보 조회를 성공했습니다.", result);
    }

    @PatchMapping("/image")
    public ApiResponse<Void> updateImage(
            @AuthenticationPrincipal UserUserDetails currentUser,
            @RequestBody @Valid UpdateProfileImageRequestDTO req
    ) {
        Long userId = currentUser.getUser().getId();
        service.updateProfileImage(userId, req.getFileKey());
        return new ApiResponse<>(200, "프로필 이미지가 성공적으로 변경되었습니다.", null);
    }

    @PatchMapping("/nickname")
    public ApiResponse<Void> updateNickname(
            @AuthenticationPrincipal UserUserDetails currentUser,
            @RequestBody @Valid UpdateNicknameRequestDTO req
    ) {
        Long userId = currentUser.getUser().getId();
        service.updateNickname(userId, req.getNickname());
        return new ApiResponse<>(200, "닉네임이 성공적으로 변경되었습니다.", null);
    }
}
