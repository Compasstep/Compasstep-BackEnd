package com.fifo.compasstep.retrainingData.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter @Setter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true) // 알 수 없는 필드가 와도 무시
public class UpdateInvalidRequestDTO {

    // 리스트 자체는 null 아니고 최소 1개 이상
    @NotNull(message = "finalLabels는 null일 수 없습니다.")
    @Size(min = 1, max = 50, message = "finalLabels는 1~50개여야 합니다.")
    // 리스트의 각 원소에 대한 검증 (빈 문자열/공백 금지, 길이 제한)
    private List<
            @NotBlank(message = "감정 라벨은 공백일 수 없습니다.")
            @Size(max = 50, message = "감정 라벨은 최대 50자입니다.")
                    String
            > finalLabels;
}
