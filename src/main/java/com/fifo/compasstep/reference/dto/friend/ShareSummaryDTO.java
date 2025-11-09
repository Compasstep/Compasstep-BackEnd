package com.fifo.compasstep.reference.dto.friend;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ShareSummaryDTO {

    // JSON에서 1, 0, 0 (정수)로 왔으므로 int로 받습니다.
    @JsonProperty("positive")
    private int positive;

    @JsonProperty("negative")
    private int negative;

    @JsonProperty("neutral")
    private int neutral;
}
