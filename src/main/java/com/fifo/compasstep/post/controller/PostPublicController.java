// src/main/java/com/fifo/compasstep/post/controller/PostPublicController.java
package com.fifo.compasstep.post.controller;

import com.fifo.compasstep.apipayload.ApiResponse;
import com.fifo.compasstep.post.dto.response.PostDetailResponse;
import com.fifo.compasstep.post.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class PostPublicController {

    private final PostService postService;

    /** 게시글 상세 조회 (게스트 접근 가능) */
    @GetMapping("/posts/{postId}")
    public ApiResponse<PostDetailResponse> getPostDetail(@PathVariable Long postId) {
        var detail = postService.getPostDetail(postId);
        return new ApiResponse<>(200, "지인 평가 상세 조회를 성공했습니다.", detail);
    }
}
