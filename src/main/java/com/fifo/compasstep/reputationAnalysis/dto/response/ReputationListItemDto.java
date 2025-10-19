package com.fifo.compasstep.reputationAnalysis.dto.response;

import lombok.Builder;

import java.time.Instant;

@Builder
public record ReputationListItemDto(
        Long historyId,
        String songTitle,
        String artistName,
        Instant createdAt
) {}