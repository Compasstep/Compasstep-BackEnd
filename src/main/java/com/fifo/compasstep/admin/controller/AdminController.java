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

import java.util.List;

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

    // 임시 비밀번호를 실제 비밀번호로 변경
    @PatchMapping("/password/change")
    @PreAuthorize("hasRole('GENERAL')")
    public ApiResponse<Void> changePassword(
            @Valid @RequestBody AdminRequestDTO.ChangePasswordRequestDTO request) {
        adminService.changePassword(request);
        return ApiResponse.success(null);
    }

    // 관리자 초대는 루트만 가능
    @PostMapping("/password/invite")
    @PreAuthorize("hasRole('ROOT')")
    public ApiResponse<Void> inviteAdmin(
            @RequestBody AdminRequestDTO.InviteRequestDTO request){
        adminService.invite(request);
        return ApiResponse.success(null);
    }

    //루트관리자가 타 관리자 비밀번호 재발급
    @PatchMapping("/password/reissue")
    @PreAuthorize("hasRole('ROOT')")
    public ApiResponse<Void> reissueAdmin(
            @Valid @RequestBody AdminRequestDTO.ReissueRequestDTO request){
        adminService.reissue(request);
        return ApiResponse.success(null);
    }

    // 관리자 전체 목록 조회
    @GetMapping("/admins")
    @PreAuthorize("hasRole('ROOT')")
    public ApiResponse<AdminResponseDTO.AdminListResponseDTO> getAllAdmins() {
        AdminResponseDTO.AdminListResponseDTO result = adminService.getAllAdmins();
        return ApiResponse.success(result);
    }

    @GetMapping("/users/malicious")
    public ApiResponse<List<AdminResponseDTO.MaliciousResponseDTO>> getMaliciousUsers() {
        List<AdminResponseDTO.MaliciousResponseDTO> result = adminService.getMaliciousUsers();
        return  ApiResponse.success(result);
    }

    @GetMapping("users/logs/{userPKId}")
    public ApiResponse<AdminResponseDTO.ChatLogsListDTO> getUserChatLogs(@PathVariable Long userPKId) {
        AdminResponseDTO.ChatLogsListDTO result = adminService.getUserChatLogs(userPKId);
        return ApiResponse.success(result);
    }

    @PatchMapping("/users/ban/{userPKId}")
    public ApiResponse<Void> banUser(@PathVariable Long userPKId) {
        adminService.banUser(userPKId);
        return ApiResponse.success(null);
    }

    @PatchMapping("/users/unban/{userPKId}")
    public ApiResponse<Void> unbanUser(@PathVariable Long userPKId) {
        adminService.unbanUser(userPKId);
        return ApiResponse.success(null);
    }

    @GetMapping("/users/dashboard")
    public ApiResponse<AdminResponseDTO.dashboardResponseDTO> dashboard() {
        AdminResponseDTO.dashboardResponseDTO result = adminService.getDashboard();
        return ApiResponse.success(result);
    }
}
