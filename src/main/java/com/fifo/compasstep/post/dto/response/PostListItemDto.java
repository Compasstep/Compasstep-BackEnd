// src/main/java/com/fifo/compasstep/post/dto/response/PostListItemDto.java
package com.fifo.compasstep.post.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PostListItemDto {
    private Long postId;
    private String songTitle;
}
