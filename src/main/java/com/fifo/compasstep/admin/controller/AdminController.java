package com.fifo.compasstep.admin.controller;

import com.fifo.compasstep.admin.dto.AdminRequestDTO;
import com.fifo.compasstep.admin.dto.AdminResponseDTO;
import com.fifo.compasstep.admin.service.AdminService;
import com.fifo.compasstep.apipayload.ApiResponse;
import com.fifo.compasstep.security.userDetails.AdminUserDetails;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {
    private final AdminService adminService;

    //나중에 스웨거때 필요
    //@Override
    @PostMapping("/login")
    public ApiResponse<AdminResponseDTO.LoginResponseDTO> login(
            @Validated @RequestBody AdminRequestDTO.AdminLoginRequestDTO request,
            HttpServletResponse response) {
        AdminResponseDTO.LoginResponseDTO result = adminService.login(request, response);
        return ApiResponse.success(result);
    }

    @PostMapping("/logout")
    public ApiResponse<Void> logout(HttpServletRequest request, HttpServletResponse response) {
        adminService.logout(request, response);
        return ApiResponse.success(null);
    }

    // ROOT만 사용 가능한 Api
    // @PreAuthorize("hasRole('ROOT')")

    @PostMapping("/refresh")
    public ApiResponse<AdminResponseDTO.LoginResponseDTO> refresh(HttpServletRequest request, HttpServletResponse response) {
        AdminResponseDTO.LoginResponseDTO result = adminService.refreshToken(request, response);
        return ApiResponse.success(result);
    }

    @DeleteMapping("/admins/{userPKId}")
    @PreAuthorize("hasRole('ROOT')")
    public ApiResponse<Void> deleteAdmin(@AuthenticationPrincipal AdminUserDetails currentAdmin, @PathVariable Long userPKId) {
        Long currentAdminId = currentAdmin.getAdmin().getId();
        adminService.deleteAdmin(currentAdminId, userPKId);
        return ApiResponse.success(null);
    }

}
