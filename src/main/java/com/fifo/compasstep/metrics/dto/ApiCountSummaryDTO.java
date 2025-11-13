package com.fifo.compasstep.metrics.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ApiCountSummaryDTO {

    // 개별 API별 호출 수
    private List<ApiCountDTO> apiCounts;

    // 전체 합산 호출 수
    private long totalCount;
}
