package com.fifo.compasstep.metrics.utils;

import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;

@Slf4j
public class MetricsUtils {

    // Double로 변환하는 유틸리티 함수
    public static Double parseDoubleValue(Object value) {
        if (value instanceof String) {
            try {
                return Double.parseDouble((String) value);
            } catch (NumberFormatException e) {
                log.error("Error parsing value to Double: {}", value);
                return null;  // 오류가 발생하면 null 반환
            }
        } else if (value instanceof Number) {
            return ((Number) value).doubleValue();
        } else {
            log.error("Unexpected value type: {}", value.getClass());
            return null;
        }
    }

    // metric에서 apiPath를 추출하는 유틸리티 함수
    public static String extractMetricToString(Map<String, Object> entry) {
        Map<String, Object> metric = (Map<String, Object>) entry.get("metric");
        return metric != null ? metric.toString() : "";
    }

    // value에서 count를 추출하는 유틸리티 함수
    public static Double extractValue(List<Object> value) {
        if (value != null && value.size() > 1) {
            return parseDoubleValue(value.get(1));  // 두 번째 값이 count 또는 latency 값
        }
        return null;
    }

    // 특정 URL을 제외한 리스트 반환 (필터링 함수)
    public static boolean shouldExcludeUrl(String apiPath, List<String> excludedUrls) {
        for (String excludedUrl : excludedUrls) {
            if (apiPath.contains(excludedUrl)) {
                return true;  // 제외할 URL을 찾으면 true 반환
            }
        }
        return false;  // 제외할 URL이 없다면 false
    }
}