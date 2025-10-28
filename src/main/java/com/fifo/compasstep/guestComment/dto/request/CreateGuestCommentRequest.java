// src/main/java/com/fifo/compasstep/guestComment/dto/request/CreateGuestCommentRequest.java
package com.fifo.compasstep.guestComment.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CreateGuestCommentRequest {
    @NotBlank
    private String comment;
    private Integer rate; // null이면 0 처리
}
