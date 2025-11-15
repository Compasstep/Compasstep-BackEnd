// src/main/java/com/fifo/compasstep/post/dto/response/PostDetailResponse.java
package com.fifo.compasstep.post.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.util.List;

@Getter @Builder
public class PostDetailResponseDTO {
    private Long postId;
    private String songTitle;
    private String artistName;
    private String s3FileKey;
    private String artistProfileImage;
    private boolean analyzed;
    private List<CommentItem> comments;

    @Getter @Builder
    public static class CommentItem {
        private Long commentId;
        private String comment;
        private Integer rate;
        private Instant createdAt;
    }
}
