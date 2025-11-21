package com.fifo.compasstep.reference.dto.friend;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ShareSummaryDTO {

    // JSON에서는 소수로 와서 수정해놓음.
    @JsonProperty("positive")
    private double positive;

    @JsonProperty("negative")
    private double negative;

    @JsonProperty("neutral")
    private double neutral;
}
