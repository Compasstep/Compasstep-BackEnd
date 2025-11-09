package com.fifo.compasstep.reference.dto.youtube;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class SummaryDTO {
    /**
     * "songTitle": "라일락"
     */
    @JsonProperty("songTitle")
    private String songTitle;

    /**
     * "sentimentSummary": { "positive": 0.85, ... }
     */
    @JsonProperty("sentimentSummary")
    private SentimentSummaryDTO sentimentSummary;

    /**
     * "emotionDetails": { "joy_happiness": 0.13..., ... }
     */
    @JsonProperty("emotionDetails")
    private EmotionDetailsDTO emotionDetails;

    /**
     * "keywords": [ "train", "lilac", ... ]
     */
    @JsonProperty("keywords")
    private List<String> keywords;
}
