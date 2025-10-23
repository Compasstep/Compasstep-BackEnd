package com.fifo.compasstep.admin.dto;

import lombok.Builder;
import lombok.Getter;

public class AdminResponseDTO {
    @Getter
    @Builder
    public static class LoginResponseDTO {
        //private String accessToken;
        private String csrfToken;
    }

}
