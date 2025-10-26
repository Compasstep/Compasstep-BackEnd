package com.fifo.compasstep.user.profile.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserProfileInfoResponseDTO {
    private String profileImageUrl; // DB에 저장된 s3FileImage 그대로 반환
    private String email;
    private String nickname;
}