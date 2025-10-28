package com.fifo.compasstep.post.repository;

import com.fifo.compasstep.post.domain.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long> {

    // 마이페이지: 특정 유저의 게시글 (Song.user.id 기준) 최신순
    List<Post> findBySong_User_IdOrderByCreatedAtDesc(Long userId);

    Optional<Post> findBySong_Id(Long songId); // 중복생성 방지 용도

    Optional<Post> findByIdAndSong_User_Id(Long postId, Long userId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("delete from Post p where p.id = :postId and p.song.user.id = :userId")
    int deleteByIdAndOwner(@Param("postId") Long postId, @Param("userId") Long userId);
}