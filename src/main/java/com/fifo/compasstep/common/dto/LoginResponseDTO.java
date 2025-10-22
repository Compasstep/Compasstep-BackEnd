package com.fifo.compasstep.common.dto;

import lombok.Builder;
import lombok.Getter;

// 두 방식의 로그인이 공용으로 사용하는 DTO
@Getter
@Builder
public class LoginResponseDTO {
    private String csrfToken;
}
