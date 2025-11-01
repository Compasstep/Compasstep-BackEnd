// src/main/java/com/fifo/compasstep/reputationAnalysis/service/ReputationAnalysisService.java
package com.fifo.compasstep.reputationAnalysis.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fifo.compasstep.apipayload.exceptions.handler.ReputationAnalysisHandler;
import com.fifo.compasstep.reputationAnalysis.domain.ReputationAnalysis;
import com.fifo.compasstep.reputationAnalysis.domain.ReputationAnalysisRepository;
import com.fifo.compasstep.reputationAnalysis.dto.response.ReputationDetailResponseDto;
import com.fifo.compasstep.reputationAnalysis.dto.response.ReputationListItemDto;
import com.fifo.compasstep.reputationAnalysis.exceptions.ReputationErrorStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReputationAnalysisService {

    private final ReputationAnalysisRepository repo;
    private final ObjectMapper objectMapper;

    /** 대중 평판 목록 조회 */
    public List<ReputationListItemDto> getList(Long userId) {
        List<ReputationAnalysis> rows = repo.findByUserIdOrderByCreatedAtDesc(userId);
        return rows.stream()
                .map(e -> ReputationListItemDto.builder()
                        .historyId(e.getId())
                        .songTitle(e.getSongTitle())
                        .artistName(e.getArtistName())
                        .createdAt(toInstant(e.getCreatedAt()))
                        .build())
                .toList();
    }

    /** 대중 평판 상세 조회 */
    public ReputationDetailResponseDto getDetail(Long historyId, Long userId) {
        // 소유자 확인
        if (!repo.existsByIdAndUserId(historyId, userId)) {
            throw new ReputationAnalysisHandler(ReputationErrorStatus.FORBIDDEN_ACCESS);
        }

        ReputationAnalysis entity = repo.findById(historyId)
                .orElseThrow(() -> new ReputationAnalysisHandler(ReputationErrorStatus.HISTORY_NOT_FOUND));

        Map<String, Double> sentiment = parseObject(entity.getSentimentSummary());
        Map<String, Double> emotions  = parseObject(entity.getEmotionDetails());
        List<String> keywords          = parseArray(entity.getKeywords());

        return ReputationDetailResponseDto.builder()
                .historyId(entity.getId())
                .songTitle(entity.getSongTitle())
                .artistName(entity.getArtistName())
                .sentimentSummary(sentiment)
                .emotionDetails(emotions)
                .keywords(keywords)
                .createdAt(toInstant(entity.getCreatedAt()))
                .build();
    }

    /** 대중 평판 삭제 */
    @Transactional
    public void delete(Long historyId, Long userId) {
        if (!repo.existsByIdAndUserId(historyId, userId)) {
            throw new ReputationAnalysisHandler(ReputationErrorStatus.FORBIDDEN_ACCESS);
        }
        ReputationAnalysis found = repo.findById(historyId)
                .orElseThrow(() -> new ReputationAnalysisHandler(ReputationErrorStatus.HISTORY_NOT_FOUND));
        repo.delete(found);
    }

    /* ===== JSON 유틸 ===== */
    private Map<String, Double> parseObject(String json) {
        try {
            return objectMapper.readValue(json, new TypeReference<>() {});
        } catch (Exception e) {
            throw new ReputationAnalysisHandler(ReputationErrorStatus.JSON_PARSE_ERROR);
        }
    }

    private List<String> parseArray(String json) {
        try {
            return objectMapper.readValue(json, new TypeReference<>() {});
        } catch (Exception e) {
            throw new ReputationAnalysisHandler(ReputationErrorStatus.JSON_PARSE_ERROR);
        }
    }

    // LocalDateTime → Instant 변환 (KST 기준; 필요 시 UTC로 변경)
    private static Instant toInstant(LocalDateTime ldt) {
        return ldt.atOffset(ZoneOffset.ofHours(9)).toInstant();
        // return ldt.atOffset(ZoneOffset.UTC).toInstant();
    }
}
