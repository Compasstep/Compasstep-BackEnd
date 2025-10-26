// src/main/java/com/fifo/compasstep/lyrics_analysis/dto/response/AnalysisDataItemDto.java
package com.fifo.compasstep.lyrics_analysis.dto.response;

import lombok.Builder;

import java.util.List;

@Builder
public record AnalysisDataItemDTO(
        String part,
        List<String> emotions,
        String coaching
) {}
