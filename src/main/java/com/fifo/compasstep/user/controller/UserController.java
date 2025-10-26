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

    //@Override
    @PostMapping("/auth/login")
    public ApiResponse<UserResponseDTO.LoginResponseDTO> login(
            @RequestBody UserRequestDTO.LoginRequestDTO request,
            HttpServletResponse response) {
        UserResponseDTO.LoginResponseDTO result = userService.login(request, response);
        return ApiResponse.success(result);

    }
    // STATUS_NORMAL인 유저만 사용 가능한 api
    // @PreAuthorize("hasAuthority('STATUS_NORMAL')")

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

    @PostMapping("/files/url")
    public ApiResponse<UserResponseDTO.generatePresignedUrlResponseDTO> generatePresignedUrl(
            UserRequestDTO.generatePresignedUrlRequestDTO request, HttpServletResponse response) {
        UserResponseDTO.generatePresignedUrlResponseDTO result = userService.generateUrl(request, response);
        return ApiResponse.success(result);
    }

    @PostMapping("/files/download")
    public ApiResponse<UserResponseDTO.downloadUrlResponseDTO> download(
            @RequestBody UserRequestDTO.downloadUrlRequestDTO request) {
        UserResponseDTO.downloadUrlResponseDTO result = userService.generateDownloadUrl(request);
        return ApiResponse.success(result);
    }
}
