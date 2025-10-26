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
        return ApiResponse.success(result);
    }

    @PatchMapping("/image")
    public ApiResponse<Void> updateImage(
            @AuthenticationPrincipal UserUserDetails currentUser,
            @RequestBody @Valid UpdateProfileImageRequestDTO req
    ) {
        Long userId = currentUser.getUser().getId();
        service.updateProfileImage(userId, req.getFileKey());
        return ApiResponse.success(null);
    }

    @PatchMapping("/nickname")
    public ApiResponse<Void> updateNickname(
            @AuthenticationPrincipal UserUserDetails currentUser,
            @RequestBody @Valid UpdateNicknameRequestDTO req
    ) {
        Long userId = currentUser.getUser().getId();
        service.updateNickname(userId, req.getNickname());
        return ApiResponse.success(null);
    }
}
