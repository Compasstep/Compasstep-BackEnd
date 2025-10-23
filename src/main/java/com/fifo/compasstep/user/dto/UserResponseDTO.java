package com.fifo.compasstep.user.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

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
}
