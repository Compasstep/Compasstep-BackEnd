package com.fifo.compasstep.user.dto;

import lombok.*;

public class UserResponseDTO {
    @Getter
    @Builder
    public static class LoginResponseDTO {
        private String csrfToken;
    }

    @Getter
    @Builder
    public static class generatePresignedUrlResponseDTO {
        @NonNull
        private String presignedUrl;
        @NonNull
        private String fileKey;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class downloadUrlResponseDTO {
        @NonNull
        private String presignedUrl;
    }
}
