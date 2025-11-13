package com.fifo.compasstep.metrics.service;

import com.fifo.compasstep.metrics.client.MetricsClient;
import com.fifo.compasstep.metrics.constants.ExcludedUrls;
import com.fifo.compasstep.metrics.dto.ApiCountDto;
import com.fifo.compasstep.metrics.dto.ApiCountSummaryDto;
import com.fifo.compasstep.metrics.dto.ApiLatencyDto;
import com.fifo.compasstep.metrics.utils.MetricsUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.*;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class MetricsService {

    private final MetricsClient metricsClient;
    // API 호출 횟수 가져오기 (내림차순)
    public List<ApiCountDto> getApiCount() {
        // 1시간 동안의 요청 횟수 증가를 계산
        String query = "sum by (uri, method) (increase(http_server_requests_seconds_count[1h]))";
        Map<String, Object> result = metricsClient.queryMetrics(query);  // Map으로 받음

        log.info("API Count Result: {}", result);  // 쿼리 결과 로그 출력

        List<ApiCountDto> apiCount = new ArrayList<>();

        Map<String, Object> data = (Map<String, Object>) result.get("data");
        if (data != null) {
            List<Map<String, Object>> resultList = (List<Map<String, Object>>) data.get("result");

            if (resultList == null || resultList.isEmpty()) {
                log.warn("No data found for the query: {}", query);
                return apiCount;  // 빈 데이터가 있으면 빈 리스트 반환
            }

            // resultList에서 api_path와 count 값을 추출
            for (Map<String, Object> entry : resultList) {
                String apiPath = MetricsUtils.extractMetricToString(entry);  // metric 처리

                // 특정 URL을 제외
                if (MetricsUtils.shouldExcludeUrl(apiPath, ExcludedUrls.EXCLUDED_URLS)) {
                    continue;  // 제외할 URL인 경우, 해당 항목을 건너뜀
                }

                List<Object> value = (List<Object>) entry.get("value");

                Double parsedValue = MetricsUtils.extractValue(value);
                if (parsedValue != null) {
                    // 소수점 제거: 호출 횟수를 정수로 변환
                    int countValue = parsedValue.intValue();  // 소수점 이하 버리기
                    apiCount.add(new ApiCountDto(apiPath, (double) countValue));  // 정수로 처리
                }
            }
        }

        // 내림차순 정렬 유지 (숫자 내림차순)
        apiCount.sort(Comparator.comparing(ApiCountDto::getCount).reversed());

        return apiCount;
    }
    public ApiCountSummaryDto getTodayApiCountSummary() {
        ZoneId zone = ZoneId.of("Asia/Seoul");

        // 오늘 00시
        ZonedDateTime now = ZonedDateTime.now(zone);
        ZonedDateTime midnight = now.toLocalDate().atStartOfDay(zone);

        // 오늘 00시부터 지금까지 경과 시간 (초)
        long seconds = Duration.between(midnight, now).getSeconds();
        if (seconds <= 0) {
            return new ApiCountSummaryDto(Collections.emptyList(), 0L);
        }

        String promDuration = seconds + "s"; // ex) "57600s" (16시간)

        String query = String.format(
                "sum by (uri, method) (increase(http_server_requests_seconds_count[%s]))",
                promDuration
        );

        log.info("[Today] API Count Query = {}", query);
        Map<String, Object> result = metricsClient.queryMetrics(query);

        return parseApiCountSummary(query, result);
    }

    // 특정 일자 기준 (해당 날짜 00:00 ~ 24:00)
    public ApiCountSummaryDto getApiCountSummaryByDate(LocalDate date) {
        ZoneId zone = ZoneId.of("Asia/Seoul");

        // date 하루치 = date 00:00 ~ date+1 00:00
        ZonedDateTime endOfDayLocal = date.plusDays(1).atStartOfDay(zone);
        Instant endOfDayUtc = endOfDayLocal.toInstant();

        String query = "sum by (uri, method) (increase(http_server_requests_seconds_count[1d]))";

        log.info("[ByDate] date = {}, API Count Query = {}, time = {}",
                date, query, endOfDayUtc.getEpochSecond());

        Map<String, Object> result = metricsClient.queryMetrics(query, endOfDayUtc);

        return parseApiCountSummary(query, result);
    }

    // 공통 파싱 로직:
    //  - 개별 ApiCountDto 리스트
    //  - totalCount (전부 합)
    @SuppressWarnings("unchecked")
    private ApiCountSummaryDto parseApiCountSummary(String query, Map<String, Object> result) {
        List<ApiCountDto> apiCounts = new ArrayList<>();
        long totalCount = 0L;

        if (result == null) {
            log.warn("Result is null for query: {}", query);
            return new ApiCountSummaryDto(apiCounts, totalCount);
        }

        Map<String, Object> data = (Map<String, Object>) result.get("data");
        if (data == null) {
            log.warn("No 'data' field in result for query: {}", query);
            return new ApiCountSummaryDto(apiCounts, totalCount);
        }

        List<Map<String, Object>> resultList =
                (List<Map<String, Object>>) data.get("result");

        if (resultList == null || resultList.isEmpty()) {
            log.warn("No 'result' list for query: {}", query);
            return new ApiCountSummaryDto(apiCounts, totalCount);
        }

        for (Map<String, Object> entry : resultList) {
            String apiPath = MetricsUtils.extractMetricToString(entry);

            // 특정 URL 제외
            if (MetricsUtils.shouldExcludeUrl(apiPath, ExcludedUrls.EXCLUDED_URLS)) {
                continue;
            }

            List<Object> value = (List<Object>) entry.get("value");
            Double parsedValue = MetricsUtils.extractValue(value);

            if (parsedValue != null) {
                int countValue = parsedValue.intValue(); // 소수점 버리고 정수로
                apiCounts.add(new ApiCountDto(apiPath, (double) countValue));

                // 총합 계산
                totalCount += countValue;
            }
        }

        // 개별 API는 내림차순 정렬
        apiCounts.sort(Comparator.comparing(ApiCountDto::getCount).reversed());

        log.info("Parsed {} APIs, totalCount = {}", apiCounts.size(), totalCount);

        return new ApiCountSummaryDto(apiCounts, totalCount);
    }

    // API 지연 시간 가져오기 (오름차순)  - 1시간
    public List<ApiLatencyDto> getApiLatency() {
        // 둘을 나눠서 지연시간 계산
        String sumQuery = "sum by (uri, method) (increase(http_server_requests_seconds_sum[1h]))"; // 1시간 동안 API 요청에 소요된 총 시간
        String countQuery = "sum by (uri, method) (increase(http_server_requests_seconds_count[1h]))"; // 1시간 동안의 요청 횟수 증가를 계산

        log.info("Executing Sum Query: {}", sumQuery);
        log.info("Executing Count Query: {}", countQuery);

        Map<String, Object> sumResult = metricsClient.queryMetrics(sumQuery);
        Map<String, Object> countResult = metricsClient.queryMetrics(countQuery);

        log.info("Sum Result: {}", sumResult);
        log.info("Count Result: {}", countResult);

        List<ApiLatencyDto> apiLatency = new ArrayList<>();

        Map<String, Object> sumData = (Map<String, Object>) sumResult.get("data");
        Map<String, Object> countData = (Map<String, Object>) countResult.get("data");

        if (sumData != null && countData != null) {
            List<Map<String, Object>> sumList = (List<Map<String, Object>>) sumData.get("result");
            List<Map<String, Object>> countList = (List<Map<String, Object>>) countData.get("result");

            for (Map<String, Object> sumEntry : sumList) {
                String apiPath = MetricsUtils.extractMetricToString(sumEntry);  // metric 처리

                // 특정 URL을 제외
                if (MetricsUtils.shouldExcludeUrl(apiPath, ExcludedUrls.EXCLUDED_URLS)) {
                    continue;  // 제외할 URL인 경우, 해당 항목을 건너뜀
                }

                Object sumValue = sumEntry.get("value");

                if (sumValue instanceof List) {
                    List<Object> sumValues = (List<Object>) sumValue;
                    Double latency = MetricsUtils.parseDoubleValue(sumValues.get(1));

                    for (Map<String, Object> countEntry : countList) {
                        if (countEntry.get("metric").toString().equals(apiPath)) {
                            Object countValue = countEntry.get("value");
                            Double count = MetricsUtils.parseDoubleValue(((List<Object>) countValue).get(1));

                            if (latency != null && count != null && count != 0) {
                                double latencyPerRequest = latency / count; // 지연 시간 계산
                                apiLatency.add(new ApiLatencyDto(apiPath, latencyPerRequest));
                            }
                        }
                    }
                }
            }
        }

        log.info("Final API Latency List: {}", apiLatency);

        // 오름차순으로 정렬 (지연 시간이 적은 순서대로)
        apiLatency.sort(Comparator.comparing(ApiLatencyDto::getLatency));

        return apiLatency;
    }
}
