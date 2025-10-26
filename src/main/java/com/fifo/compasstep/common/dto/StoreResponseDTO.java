package com.fifo.compasstep.common.dto;

import lombok.Builder;
import lombok.Getter;

// 가사 혹은 노래를 저장하고 반환해주는 공통 dto
@Getter
@Builder
public class StoreResponseDTO {
    private Long content_id;
}
