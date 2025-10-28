// src/main/java/com/fifo/compasstep/post/controller/PostController.java
package com.fifo.compasstep.post.controller;

import com.fifo.compasstep.apipayload.ApiResponse;
import com.fifo.compasstep.post.dto.request.CreatePostRequest;
import com.fifo.compasstep.post.dto.response.CreatePostResponse;
import com.fifo.compasstep.post.dto.response.PostListItemDto;
import com.fifo.compasstep.post.service.PostService;
import com.fifo.compasstep.security.userDetails.UserUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user/mypage")
public class PostController {

    private final PostService postService;

    /** 게시글 생성 */
    @PostMapping("/posts")
    public ApiResponse<CreatePostResponse> createPost(
            @AuthenticationPrincipal UserUserDetails currentUser,
            @Valid @RequestBody CreatePostRequest request
    ) {
        Long userId = currentUser.getUser().getId();
        var result = postService.createPost(userId, request);
        return new ApiResponse<>(200, "지인 평가 공유 게시글을 성공적으로 생성했습니다.", result);
    }

    /** 게시글 목록 조회 (마이페이지) */
    @GetMapping("/posts/me")
    public ApiResponse<List<PostListItemDto>> getMyPosts(
            @AuthenticationPrincipal UserUserDetails currentUser
    ) {
        Long userId = currentUser.getUser().getId();
        var list = postService.getMyPosts(userId);
        return new ApiResponse<>(200, "나의 지인 평가 목록 조회를 성공했습니다.", list);
    }

    /** 게시글 삭제 */
    @DeleteMapping("/posts/{postId}")
    public ApiResponse<Void> deletePost(
            @PathVariable Long postId,
            @AuthenticationPrincipal UserUserDetails currentUser
    ) {
        Long userId = currentUser.getUser().getId();
        postService.deletePost(userId, postId);
        return new ApiResponse<>(200, "성공했습니다", null);
    }
}
