package com.fifo.compasstep.lyrics_analysis.dto.response;

import lombok.Builder;

import java.util.List;

@Builder
public record LyricsAnalysisListResponseDTO(
        List<LyricsAnalysisListItemDTO> analyses
) {}
