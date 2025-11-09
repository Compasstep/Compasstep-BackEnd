package com.fifo.compasstep.reference.dto.LyricsAnalysis;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.util.List;

@Getter
@NoArgsConstructor // JSON 역직렬화를 위한 기본 생성자
public class LyricsAnalysisDTO {

    /**
     * "part": "떠나는 길에 네가 내게 말했지"
     */
    @JsonProperty("part")
    private String part;

    /**
     * "emotions": [ "realization", "sadness_grief" ]
     */
    @JsonProperty("emotions")
    private List<String> emotions;

    /**
     * "coaching": "‘떠나는’에서 호흡을 길게 빼며 시작하고..."
     */
    @JsonProperty("coaching")
    private String coaching;
}