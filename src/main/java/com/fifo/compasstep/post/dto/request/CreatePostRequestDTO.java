// src/main/java/com/fifo/compasstep/post/dto/request/CreatePostRequest.java
package com.fifo.compasstep.post.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CreatePostRequestDTO {
    @NotNull
    private Long songId;
}
