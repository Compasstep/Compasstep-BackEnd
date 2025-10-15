// src/main/java/com/fifo/compasstep/lyrics_analysis/service/LyricsAnalysisService.java
package com.fifo.compasstep.lyrics_analysis.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fifo.compasstep.apipayload.exceptions.GeneralException;
import com.fifo.compasstep.lyrics_analysis.domain.LyricsAnalysisRepository;
import com.fifo.compasstep.lyrics_analysis.dto.response.LyricsAnalysisDetailResponseDTO;
import com.fifo.compasstep.lyrics_analysis.dto.response.LyricsAnalysisListItemDTO;
import com.fifo.compasstep.lyrics_analysis.dto.response.LyricsAnalysisListResponseDTO;
import com.fifo.compasstep.lyrics_analysis.dto.response.AnalysisResultDTO;
import com.fifo.compasstep.lyrics_analysis.exceptions.LyricsAnalysisErrorStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LyricsAnalysisService {

    private final LyricsAnalysisRepository repo;
    private final ObjectMapper objectMapper;

    /** 가사 분석 목록 조회 (userId 기준) */
    public LyricsAnalysisListResponseDTO getList(Long userId) {
        var rows = repo.findListByUserId(userId);
        List<LyricsAnalysisListItemDTO> items = rows.stream()
                .map(r -> LyricsAnalysisListItemDTO.builder()
                        .lyricsAnalysisId(r.getLyricsAnalysisId())
                        .lyricsTitle(r.getLyricsTitle())
                        .createdAt(r.getCreatedAt())
                        .build()
                ).toList();

        return LyricsAnalysisListResponseDTO.builder()
                .analyses(items)
                .build();
    }

    /** 가사 분석 상세 조회 (analysisId + userId) */
    public LyricsAnalysisDetailResponseDTO getDetail(Long analysisId, Long userId) {
        // 소유자 확인 (임시 인증 단계에서도 보안용으로 유지 권장)
        if (repo.notExistsByIdAndUserId(analysisId, userId)) {
            throw new GeneralException(LyricsAnalysisErrorStatus.FORBIDDEN_ACCESS);
        }

        var row = repo.findDetailById(analysisId)
                .orElseThrow(() -> new GeneralException(LyricsAnalysisErrorStatus.ANALYSIS_NOT_FOUND));

        AnalysisResultDTO parsed = parseResult(row.getAnalysisResult());

        return LyricsAnalysisDetailResponseDTO.builder()
                .lyricsAnalysisId(row.getLyricsAnalysisId())
                .lyricsTitle(row.getLyricsTitle())
                .createdAt(row.getCreatedAt())
                .analysisResult(parsed)
                .build();
    }

    /** 저장된 가사 분석 삭제 (analysisId + userId) */
    @Transactional
    public void delete(Long analysisId, Long userId) {
        if (repo.notExistsByIdAndUserId(analysisId, userId)) {
            throw new GeneralException(LyricsAnalysisErrorStatus.FORBIDDEN_ACCESS);
        }
        // 존재 여부 한 번 더 확인 (선택)
        var found = repo.findById(analysisId)
                .orElseThrow(() -> new GeneralException(LyricsAnalysisErrorStatus.ANALYSIS_NOT_FOUND));
        repo.delete(found);
    }

    /* 내부 유틸 */
    private AnalysisResultDTO parseResult(String json) {
        try {
            return objectMapper.readValue(json, AnalysisResultDTO.class);
        } catch (Exception e) {
            throw new GeneralException(LyricsAnalysisErrorStatus.JSON_PARSE_ERROR);
        }
    }
}
