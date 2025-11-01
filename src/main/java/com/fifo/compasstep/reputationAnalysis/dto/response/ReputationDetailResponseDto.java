package com.fifo.compasstep.reputationAnalysis.dto.response;

import lombok.Builder;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@Builder
public record ReputationDetailResponseDto(
        Long historyId,
        String songTitle,
        String artistName,
        Map<String, Double> sentimentSummary, // {"positive":85,"negative":10,"neutral":5}
        Map<String, Double> emotionDetails,   // {"joy":50,"sadness":5,...}
        List<String> keywords,                 // ["달달하다","목소리",...]
        Instant createdAt
) {}