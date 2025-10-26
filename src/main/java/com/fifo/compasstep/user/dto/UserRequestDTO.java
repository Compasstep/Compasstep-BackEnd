package com.fifo.compasstep.user.dto;

import lombok.*;

public class UserRequestDTO {
    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class LoginRequestDTO {
        @NonNull
        private String googleToken;

    }

    @Getter
    @Builder
    public static class generatePresignedUrlRequestDTO {
        @NonNull
        private String fileType;
        @NonNull
        private String contentType;
        @NonNull
        private Long fileSize;
        @NonNull
        private String originalFileName;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class downloadUrlRequestDTO {
        @NonNull
        private String fileKey;
        @NonNull
        private String originalFileName;
    }
}
