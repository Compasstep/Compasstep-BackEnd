package com.fifo.compasstep.reference.dto.youtube;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PeerAnalysisResultDTO {
    @JsonProperty("summary")
    private SummaryDTO summary;
}
