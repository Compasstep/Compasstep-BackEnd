// src/main/java/com/fifo/compasstep/guestComment/controller/GuestCommentController.java
package com.fifo.compasstep.guestComment.controller;

import com.fifo.compasstep.apipayload.ApiResponse;
import com.fifo.compasstep.guestComment.dto.request.CreateGuestCommentRequestDTO;
import com.fifo.compasstep.guestComment.service.GuestCommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class GuestCommentController {

    private final GuestCommentService guestCommentService;

    /** 댓글 작성 (게스트) */
    @PostMapping("/posts/{postId}/comments")
    public ApiResponse<Void> createComment(
            @PathVariable Long postId,
            @Valid @RequestBody CreateGuestCommentRequestDTO request
    ) {
        guestCommentService.createComment(postId, request);
        return ApiResponse.success(null);
    }
}
