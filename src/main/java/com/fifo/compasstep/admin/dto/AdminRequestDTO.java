package com.fifo.compasstep.admin.dto;

import lombok.*;
import org.hibernate.annotations.processing.Pattern;

public class AdminRequestDTO {
    @Getter
    @Builder
    public static class AdminLoginRequestDTO {
        @NonNull
        private String email;
        @NonNull
        private String password;
    }

    @Getter
    @Builder
    public static class ChangePasswordRequestDTO {
        private String password;
        private String doubleCheck;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class InviteRequestDTO {
        private String email;
    }

    @Getter
    @Builder
    public static class ReissueRequestDTO {
        private String adminPKId;
    }
}
