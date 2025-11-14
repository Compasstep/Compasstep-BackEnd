package com.fifo.compasstep.user.controller;

import com.fifo.compasstep.apipayload.ApiResponse;
import com.fifo.compasstep.user.dto.UserRequestDTO;
import com.fifo.compasstep.user.dto.UserResponseDTO;
import com.fifo.compasstep.user.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping("/auth/login")
    public ApiResponse<UserResponseDTO.LoginResponseDTO> login(
            @RequestBody UserRequestDTO.LoginRequestDTO request,
            HttpServletResponse response) {
        UserResponseDTO.LoginResponseDTO result = userService.login(request, response);
        return ApiResponse.success(result);
    }

    @PostMapping("/auth/logout")
    public ApiResponse<Void> logout(HttpServletRequest request, HttpServletResponse response) {
        userService.logout(request, response);
        return ApiResponse.success(null);
    }

    @DeleteMapping("/auth/signout")
    public ApiResponse<Void> signout(HttpServletRequest request, HttpServletResponse response) {
        userService.signout(request, response);
        return ApiResponse.success(null);
    }

    /* ------------------------------------------------------------
       🔥 presigned URL 발급 API (인증 필요)
       - @RequestBody 추가 (이전 누락 → 400/401 원인)
       - @PreAuthorize 추가 (Security rule과 일관성 유지)
    ------------------------------------------------------------- */
    @PreAuthorize("hasAnyAuthority('STATUS_NORMAL','STATUS_SUSPENDED')")
    @PostMapping("/files/url")
    public ApiResponse<UserResponseDTO.generatePresignedUrlResponseDTO> generatePresignedUrl(
            @RequestBody UserRequestDTO.generatePresignedUrlRequestDTO request) {   // ← 변경됨
        UserResponseDTO.generatePresignedUrlResponseDTO result = userService.generateUrl(request);
        return ApiResponse.success(result);
    }

    @PreAuthorize("hasAnyAuthority('STATUS_NORMAL','STATUS_SUSPENDED')")
    @PostMapping("/files/download")
    public ApiResponse<UserResponseDTO.downloadUrlResponseDTO> download(
            @RequestBody UserRequestDTO.downloadUrlRequestDTO request) {
        UserResponseDTO.downloadUrlResponseDTO result = userService.generateDownloadUrl(request);
        return ApiResponse.success(result);
    }
}
