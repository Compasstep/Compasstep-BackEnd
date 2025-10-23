package com.fifo.compasstep.user.profile.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UpdateProfileImageRequest {
    @NotBlank(message = "fileKey는 필수입니다.")
    private String fileKey;
}