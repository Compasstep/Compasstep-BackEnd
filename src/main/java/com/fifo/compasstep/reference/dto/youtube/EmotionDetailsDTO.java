package com.fifo.compasstep.reference.dto.youtube;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class EmotionDetailsDTO {
    @JsonProperty("joy_happiness")
    private double joyHappiness;

    @JsonProperty("gratitude")
    private double gratitude;

    @JsonProperty("admiration")
    private double admiration;

    @JsonProperty("interest_curiosity")
    private double interestCuriosity;

    @JsonProperty("approval")
    private double approval;

    @JsonProperty("anger_annoyance")
    private double angerAnnoyance;

    @JsonProperty("disgust")
    private double disgust;

    @JsonProperty("sadness_grief")
    private double sadnessGrief;

    @JsonProperty("fear_nervousness")
    private double fearNervousness;

    @JsonProperty("surprise")
    private double surprise;

    @JsonProperty("realization")
    private double realization;

    @JsonProperty("relief")
    private double relief;

    @JsonProperty("caring_love")
    private double caringLove;

    @JsonProperty("embarrassment")
    private double embarrassment;

    @JsonProperty("confusion")
    private double confusion;

    @JsonProperty("curiosity")
    private double curiosity;

    @JsonProperty("disapproval")
    private double disapproval;

    @JsonProperty("resolute")
    private double resolute;

    @JsonProperty("arrogance")
    private double arrogance;

    @JsonProperty("neutral_misc")
    private double neutralMisc;
}
