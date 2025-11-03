package com.fifo.compasstep.metrics.controller;

import com.fifo.compasstep.apipayload.ApiResponse;
import com.fifo.compasstep.metrics.dto.ApiCountDto;
import com.fifo.compasstep.metrics.dto.ApiLatencyDto;
import com.fifo.compasstep.metrics.service.MetricsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin")  // /api/admin 경로로만 접근 가능, 관리자만 접근할 수 있음
public class MetricsController {

    private final MetricsService metricsService;

    // API 호출 횟수 반환 (ROOT 및 GENERAL 관리자만 접근 가능)
    @GetMapping("/metrics/api-count")
    public ApiResponse<List<ApiCountDto>> getApiCount() {
        List<ApiCountDto> apiCount = metricsService.getApiCount();
        return ApiResponse.success(apiCount);
    }

    // API 지연 시간 반환 (ROOT 및 GENERAL 관리자만 접근 가능)
    @GetMapping("/metrics/api-latency")
    public ApiResponse<List<ApiLatencyDto>> getApiLatency() {
        List<ApiLatencyDto> apiLatency = metricsService.getApiLatency();
        return ApiResponse.success(apiLatency);
    }
}
