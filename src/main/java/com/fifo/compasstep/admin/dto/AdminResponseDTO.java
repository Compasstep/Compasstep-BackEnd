package com.fifo.compasstep.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

public class AdminResponseDTO {
    @Getter
    @Builder
    public static class LoginResponseDTO {
        //private String accessToken;
        private String csrfToken;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AdminListResponseDTO {
        private List<AdminInfoDTO> adminList;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AdminInfoDTO {
        private String nickname;
        private String email;
    }

}
