package com.fifo.compasstep.post.dto.response;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;

@Getter
@Builder
public class PostAnalysisResponseDTO {
    private Long postId;
    private String songTitle;
    private String artistName;
    private JsonNode shareSummary;
    private JsonNode shareDetails;
    private JsonNode keywords;
    private Instant analyzedAt;
}