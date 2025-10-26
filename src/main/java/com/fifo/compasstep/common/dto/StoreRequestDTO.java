package com.fifo.compasstep.common.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class StoreRequestDTO {
    String title;
    String fileKey;
}
