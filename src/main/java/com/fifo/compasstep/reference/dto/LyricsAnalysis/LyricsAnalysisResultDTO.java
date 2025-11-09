package com.fifo.compasstep.reference.dto.LyricsAnalysis;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.util.List;

@Getter
@NoArgsConstructor
public class LyricsAnalysisResultDTO {

    /**
     * "analysisData": [ { ... }, { ... } ]
     */
    @JsonProperty("analysisData")
    private List<LyricsAnalysisDTO> analysisData;
}