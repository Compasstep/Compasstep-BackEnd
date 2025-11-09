package com.fifo.compasstep.reference.dto.youtube;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SentimentSummaryDTO {
    /**
     * "positive": 0.85
     */
    @JsonProperty("positive")
    private double positive;

    /**
     * "negative": 0.04
     */
    @JsonProperty("negative")
    private double negative;

    /**
     * "neutral": 0.11
     */
    @JsonProperty("neutral")
    private double neutral;
}
