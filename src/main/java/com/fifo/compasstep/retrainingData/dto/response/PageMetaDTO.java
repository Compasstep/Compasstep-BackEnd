// retrainingData/dto/response/PageMetaDto.java
package com.fifo.compasstep.retrainingData.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PageMetaDTO {
    private int page;           // 현재 페이지 (0-based)
    private int size;           // 페이지 크기
    private int totalPages;     // 총 페이지 수
    private long totalElements; // 전체 아이템 수
    private boolean hasNext;    // 다음 페이지 존재 여부
    private boolean hasPrev;    // 이전 페이지 존재 여부
}
