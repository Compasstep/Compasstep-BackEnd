// src/main/java/com/fifo/compasstep/guestComment/repository/GuestCommentRepository.java
package com.fifo.compasstep.guestComment.repository;

import com.fifo.compasstep.guestComment.domain.GuestComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface GuestCommentRepository extends JpaRepository<GuestComment, Long> {

    List<GuestComment> findByPost_IdOrderByCreatedAtAsc(Long postId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("delete from GuestComment gc where gc.post.id = :postId")
    int deleteByPostId(Long postId);
}
