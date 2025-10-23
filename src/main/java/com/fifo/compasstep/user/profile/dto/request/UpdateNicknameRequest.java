package com.fifo.compasstep.user.profile.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UpdateNicknameRequest {
    @NotBlank(message = "닉네임은 비어 있을 수 없습니다.")
    @Size(max = 30, message = "닉네임은 30자 이하여야 합니다.")
    private String nickname;
}