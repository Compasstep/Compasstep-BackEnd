package com.fifo.compasstep.metrics.controller;

import com.fifo.compasstep.apipayload.ApiResponse;
import com.fifo.compasstep.metrics.dto.ApiCountDTO;
import com.fifo.compasstep.metrics.dto.ApiCountSummaryDTO;
import com.fifo.compasstep.metrics.dto.ApiLatencyDto;
import com.fifo.compasstep.metrics.service.MetricsService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin")  // 관리자 전용
public class MetricsController {

    private final MetricsService metricsService;

    //  API별 호출 횟수(최근 1시간 증가량)
    @GetMapping("/metrics/api-count")
    public ApiResponse<List<ApiCountDTO>> getApiCount() {
        List<ApiCountDTO> apiCount = metricsService.getApiCount();
        return ApiResponse.success(apiCount);
    }

    // API별 지연 시간
    @GetMapping("/metrics/api-latency")
    public ApiResponse<List<ApiLatencyDto>> getApiLatency() {
        List<ApiLatencyDto> apiLatency = metricsService.getApiLatency();
        return ApiResponse.success(apiLatency);
    }


    // 오늘(00시~지금) 기준 개별 API + 총합
    @GetMapping("/metrics/api-count/today")
    public ApiResponse<ApiCountSummaryDTO> getTodayApiCountSummary() {
        ApiCountSummaryDTO summary = metricsService.getTodayApiCountSummary();
        return ApiResponse.success(summary);
    }

    // 특정 날짜 기준 개별 API + 총합
    @GetMapping("/metrics/api-count/by-date")
    public ApiResponse<ApiCountSummaryDTO> getApiCountSummaryByDate(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate date
    ) {
        ApiCountSummaryDTO summary = metricsService.getApiCountSummaryByDate(date);
        return ApiResponse.success(summary);
    }
}
