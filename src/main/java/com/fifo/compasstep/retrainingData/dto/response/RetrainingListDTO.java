// retrainingData/dto/response/RetrainingListDto.java
package com.fifo.compasstep.retrainingData.dto.response;

import lombok.Builder;
import lombok.Value;

import java.util.List;

@Value
@Builder
public class RetrainingListDTO {
    PageMetaDTO pageMeta;
    List<RetrainingItemDTO> reviews;
}
