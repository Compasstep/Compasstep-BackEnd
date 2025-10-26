package com.fifo.compasstep.admin.dto;

import com.fifo.compasstep.user.enums.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.checkerframework.checker.units.qual.A;

import java.time.LocalDateTime;
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

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MaliciousResponseDTO {
        private Long userId;
        private String name;
        private String email;
        private Status status;
        private LocalDateTime createdAt;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ChatLogsListDTO{
        private List<ChatLogs> chatLogs;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ChatLogs {
        private Long chatId;
        private LocalDateTime createdAt;
        private String content;
        private Boolean isGuardrailed;
    }

}
