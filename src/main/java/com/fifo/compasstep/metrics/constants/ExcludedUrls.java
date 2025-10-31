package com.fifo.compasstep.metrics.constants;

import java.util.List;

public class ExcludedUrls {

    // 제외할 URL 목록을 상수로 정의 (와일드카드 경로 포함)
    public static final List<String> EXCLUDED_URLS = List.of(
            "/actuator/prometheus",
            "/swagger-ui*/**",
            "/swagger-ui*/*swagger-initializer.js",
            "/v3/api-docs",
            "/v3/api-docs/swagger-config",
            "UNKNOWN"
    );
}
