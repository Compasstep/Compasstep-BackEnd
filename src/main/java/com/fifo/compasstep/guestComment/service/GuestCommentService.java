// src/main/java/com/fifo/compasstep/guestComment/service/GuestCommentService.java
package com.fifo.compasstep.guestComment.service;

import com.fifo.compasstep.apipayload.exceptions.handler.GuestCommentHandler;
import com.fifo.compasstep.guestComment.domain.GuestComment;
import com.fifo.compasstep.guestComment.dto.request.CreateGuestCommentRequest;
import com.fifo.compasstep.guestComment.exceptions.GuestCommentErrorStatus;
import com.fifo.compasstep.guestComment.repository.GuestCommentRepository;
import com.fifo.compasstep.post.domain.Post;
import com.fifo.compasstep.post.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GuestCommentService {

    private final GuestCommentRepository guestCommentRepository;
    private final PostRepository postRepository;

    /** 댓글 작성 (게스트) */
    @Transactional
    public void createComment(Long postId, CreateGuestCommentRequest req) {
        if (req.getComment() == null || req.getComment().isBlank()) {
            throw new GuestCommentHandler(GuestCommentErrorStatus.COMMENT_REQUIRED);
        }

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new GuestCommentHandler(GuestCommentErrorStatus.POST_NOT_FOUND));

        Integer rate = (req.getRate() == null) ? 0 : req.getRate();
        GuestComment entity = GuestComment.create(post, req.getComment(), rate);
        guestCommentRepository.save(entity);
    }
}
