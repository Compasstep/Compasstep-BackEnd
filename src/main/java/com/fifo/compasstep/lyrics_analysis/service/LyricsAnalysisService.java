// src/main/java/com/fifo/compasstep/lyrics_analysis/service/LyricsAnalysisService.java
package com.fifo.compasstep.lyrics_analysis.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fifo.compasstep.apipayload.exceptions.handler.LyricsAnalysisHandler;
import com.fifo.compasstep.lyrics_analysis.domain.LyricsAnalysis;
import com.fifo.compasstep.lyrics_analysis.domain.LyricsAnalysisRepository;
import com.fifo.compasstep.lyrics_analysis.dto.response.AnalysisResultDTO;
import com.fifo.compasstep.lyrics_analysis.dto.response.LyricsAnalysisDetailResponseDTO;
import com.fifo.compasstep.lyrics_analysis.dto.response.LyricsAnalysisListItemDTO;
import com.fifo.compasstep.lyrics_analysis.dto.response.LyricsAnalysisListResponseDTO;
import com.fifo.compasstep.lyrics_analysis.exceptions.LyricsAnalysisErrorStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LyricsAnalysisService {

    private final LyricsAnalysisRepository repo;
    private final ObjectMapper objectMapper;

    /** 가사 분석 목록 조회 (userId 기준) */
    public LyricsAnalysisListResponseDTO getList(Long userId) {
        List<LyricsAnalysis> rows = repo.findByLyrics_UserIdOrderByCreatedAtDesc(userId);
        List<LyricsAnalysisListItemDTO> items = rows.stream()
                .map(e -> LyricsAnalysisListItemDTO.builder()
                        .lyricsAnalysisId(e.getId())
                        .lyricsTitle(e.getLyrics().getTitle())
                        .createdAt(toInstant(e.getCreatedAt()))
                        .build()
                ).toList();

        return LyricsAnalysisListResponseDTO.builder()
                .analyses(items)
                .build();
    }

    /** 가사 분석 상세 조회 (analysisId + userId) */
    public LyricsAnalysisDetailResponseDTO getDetail(Long analysisId, Long userId) {
        // 권한/소유자 확인
        if (!repo.existsByIdAndLyrics_UserId(analysisId, userId)) {
            throw new LyricsAnalysisHandler(LyricsAnalysisErrorStatus.FORBIDDEN_ACCESS);
        }

        LyricsAnalysis entity = repo.findById(analysisId)
                .orElseThrow(() -> new LyricsAnalysisHandler(LyricsAnalysisErrorStatus.ANALYSIS_NOT_FOUND));

        AnalysisResultDTO parsed = parseResult(entity.getAnalysisResult());

        return LyricsAnalysisDetailResponseDTO.builder()
                .lyricsAnalysisId(entity.getId())
                .lyricsTitle(entity.getLyrics().getTitle())
                .createdAt(toInstant(entity.getCreatedAt()))
                .analysisResult(parsed)
                .build();
    }

    /** 저장된 가사 분석 삭제 (analysisId + userId) */
    @Transactional
    public void delete(Long analysisId, Long userId) {
        if (!repo.existsByIdAndLyrics_UserId(analysisId, userId)) {
            throw new LyricsAnalysisHandler(LyricsAnalysisErrorStatus.FORBIDDEN_ACCESS);
        }
        LyricsAnalysis found = repo.findById(analysisId)
                .orElseThrow(() -> new LyricsAnalysisHandler(LyricsAnalysisErrorStatus.ANALYSIS_NOT_FOUND));
        repo.delete(found);
    }

    /* 내부 유틸 */
    private AnalysisResultDTO parseResult(String json) {
        try {
            return objectMapper.readValue(json, AnalysisResultDTO.class);
        } catch (Exception e) {
            throw new LyricsAnalysisHandler(LyricsAnalysisErrorStatus.JSON_PARSE_ERROR);
        }
    }

    // LocalDateTime → Instant 변환 (KST 기준)
    private static Instant toInstant(LocalDateTime ldt) {
        // 서버 표준 시간대를 KST(UTC+9)로 고정하여 변환
        return ldt.atOffset(ZoneOffset.ofHours(9)).toInstant();
        // 필요시 시스템 타임존 사용:
        // return ldt.atZone(ZoneId.systemDefault()).toInstant();
    }
}
