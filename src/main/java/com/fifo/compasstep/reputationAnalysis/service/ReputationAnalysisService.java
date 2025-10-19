package com.fifo.compasstep.reputationAnalysis.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fifo.compasstep.apipayload.exceptions.GeneralException;
import com.fifo.compasstep.reputationAnalysis.domain.ReputationAnalysisRepository;
import com.fifo.compasstep.reputationAnalysis.dto.response.ReputationDetailResponseDto;
import com.fifo.compasstep.reputationAnalysis.dto.response.ReputationListItemDto;
import com.fifo.compasstep.reputationAnalysis.exceptions.ReputationErrorStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        var rows = repo.findListByUserId(userId);
        return rows.stream()
                .map(r -> ReputationListItemDto.builder()
                        .historyId(r.getHistoryId())
                        .songTitle(r.getSongTitle())
                        .artistName(r.getArtistName())
                        .createdAt(r.getCreatedAt())
                        .build())
                .toList();
    }

    /** 대중 평판 상세 조회 */
    public ReputationDetailResponseDto getDetail(Long historyId, Long userId) {
        // 소유자 확인
        if (!repo.existsByIdAndUserId(historyId, userId)) {
            throw new GeneralException(ReputationErrorStatus.FORBIDDEN_ACCESS);
        }

        var row = repo.findDetailById(historyId)
                .orElseThrow(() -> new GeneralException(ReputationErrorStatus.HISTORY_NOT_FOUND));

        Map<String, Integer> sentiment = parseObject(row.getSentimentSummary());
        Map<String, Integer> emotions  = parseObject(row.getEmotionDetails());
        List<String> keywords          = parseArray(row.getKeywords());

        return ReputationDetailResponseDto.builder()
                .historyId(row.getHistoryId())
                .songTitle(row.getSongTitle())
                .artistName(row.getArtistName())
                .sentimentSummary(sentiment)
                .emotionDetails(emotions)
                .keywords(keywords)
                .createdAt(row.getCreatedAt())
                .build();
    }

    /** 대중 평판 삭제 */
    @Transactional
    public void delete(Long historyId, Long userId) {
        if (!repo.existsByIdAndUserId(historyId, userId)) {
            throw new GeneralException(ReputationErrorStatus.FORBIDDEN_ACCESS);
        }
        var found = repo.findById(historyId)
                .orElseThrow(() -> new GeneralException(ReputationErrorStatus.HISTORY_NOT_FOUND));
        repo.delete(found);
    }

    /* ===== JSON 유틸 ===== */
    private Map<String, Integer> parseObject(String json) {
        try {
            return objectMapper.readValue(json, new TypeReference<>() {});
        } catch (Exception e) {
            throw new GeneralException(ReputationErrorStatus.JSON_PARSE_ERROR);
        }
    }
    private List<String> parseArray(String json) {
        try {
            return objectMapper.readValue(json, new TypeReference<>() {});
        } catch (Exception e) {
            throw new GeneralException(ReputationErrorStatus.JSON_PARSE_ERROR);
        }
    }
}
