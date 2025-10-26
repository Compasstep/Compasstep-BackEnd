package com.fifo.compasstep.lyrics_analysis.dto.response;

import lombok.Builder;

import java.time.Instant;

@Builder
public record LyricsAnalysisDetailResponseDTO(
        Long lyricsAnalysisId,
        String lyricsTitle,
        Instant createdAt,
        AnalysisResultDTO analysisResult
) {}