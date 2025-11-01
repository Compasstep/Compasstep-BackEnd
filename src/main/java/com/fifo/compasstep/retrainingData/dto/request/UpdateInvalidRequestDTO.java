package com.fifo.compasstep.retrainingData.dto.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class UpdateInvalidRequestDTO {

    @NotEmpty(message = "finalLabels는 비어있을 수 없습니다.")
    private List<String> finalLabels;
}
