package com.fifo.compasstep.reference.dto.friend;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class FriendAnalysisResultDTO {

    @JsonProperty("postId")
    private Long postId; // JSON의 501에 해당

    @JsonProperty("share_summary")
    private ShareSummaryDTO shareSummary;

    @JsonProperty("share_details")
    private ShareDetailsDTO shareDetails;

    @JsonProperty("keywords")
    private List<String> keywords;
}
