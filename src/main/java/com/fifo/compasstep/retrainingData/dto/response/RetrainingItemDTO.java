// retrainingData/dto/response/RetrainingItemDto.java
package com.fifo.compasstep.retrainingData.dto.response;

import lombok.Builder;
import lombok.Value;

import java.time.ZonedDateTime;
import java.util.List;

@Value
@Builder
public class RetrainingItemDTO {
    Long reviewId;
    Boolean isLearned;
    String commentText;
    List<String> emotions;      // prediction(JSON) 파싱 결과
    Double confidence;
    ZonedDateTime createdAt;    // invalid 에서는 updatedAt=null
    ZonedDateTime updatedAt;
}
