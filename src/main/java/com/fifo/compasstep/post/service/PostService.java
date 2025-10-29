// src/main/java/com/fifo/compasstep/post/service/PostService.java
package com.fifo.compasstep.post.service;

import com.fifo.compasstep.apipayload.exceptions.handler.PostHandler;
import com.fifo.compasstep.guestComment.repository.GuestCommentRepository;
import com.fifo.compasstep.post.domain.Post;
import com.fifo.compasstep.post.dto.request.CreatePostRequest;
import com.fifo.compasstep.post.dto.response.CreatePostResponse;
import com.fifo.compasstep.post.dto.response.PostAnalysisResponse;
import com.fifo.compasstep.post.dto.response.PostDetailResponse;
import com.fifo.compasstep.post.dto.response.PostListItemDto;
import com.fifo.compasstep.post.exceptions.PostErrorStatus;
import com.fifo.compasstep.post.repository.PostRepository;
import com.fifo.compasstep.song.domain.Song;
import com.fifo.compasstep.song.repository.SongRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostService {

    private final PostRepository postRepository;
    private final SongRepository songRepository;
    private final GuestCommentRepository guestCommentRepository;

    /** 게시글 생성 */
    @Transactional
    public CreatePostResponse createPost(Long userId, CreatePostRequest req) {
        if (!songRepository.existsByIdAndUser_Id(req.getSongId(), userId)) {
            throw new PostHandler(PostErrorStatus.SONG_NOT_FOUND);
        }

        Song song = songRepository.findById(req.getSongId())
                .orElseThrow(() -> new PostHandler(PostErrorStatus.SONG_NOT_FOUND));

        postRepository.findBySong_Id(song.getId())
                .ifPresent(p -> { throw new PostHandler(PostErrorStatus.DUPLICATE_POST_FOR_SONG); });

        String postName = song.getTitle();

        // 현재 엔티티 팩토리 시그니처 유지 (shareLink는 null)
        Post post = Post.create(song, null, null, postName, null);
        post = postRepository.save(post);

        return new CreatePostResponse(post.getId());
    }

    /** 게시글 목록 조회 (마이페이지) */
    public List<PostListItemDto> getMyPosts(Long userId) {
        List<Post> posts = postRepository.findBySong_User_IdOrderByCreatedAtDesc(userId);
        return posts.stream()
                .map(p -> PostListItemDto.builder()
                        .postId(p.getId())
                        .songTitle(p.getSong().getTitle())
                        .build())
                .toList();
    }

    /** 게시글 상세 조회 (게스트 접근 가능) */
    public PostDetailResponse getPostDetail(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostHandler(PostErrorStatus.POST_NOT_FOUND));

        String title = post.getSong().getTitle();
        String s3Key = post.getSong().getS3FileKey();
        String artistName = post.getSong().getUser().getNickname() != null
                ? post.getSong().getUser().getNickname()
                : post.getSong().getUser().getName();

        var comments = guestCommentRepository.findByPost_IdOrderByCreatedAtAsc(postId).stream()
                .map(gc -> PostDetailResponse.CommentItem.builder()
                        .commentId(gc.getId())
                        .comment(gc.getComment())
                        .rate(gc.getRate())
                        .createdAt(toInstant(gc.getCreatedAt()))
                        .build())
                .toList();

        return PostDetailResponse.builder()
                .postId(post.getId())
                .songTitle(title)
                .artistName(artistName)
                .s3FileKey(s3Key)
                .analyzed(post.isAnalyzed())
                .comments(comments)
                .build();
    }

    /** 게시글 삭제 */
    @Transactional
    public void deletePost(Long userId, Long postId) {
        // 존재 체크 (없으면 404)
        postRepository.findById(postId)
                .orElseThrow(() -> new PostHandler(PostErrorStatus.POST_NOT_FOUND));

        // 자식(댓글) 먼저 정리 (벌크 삭제는 cascade/orphanRemoval이 적용 안 되므로)
        guestCommentRepository.deleteByPostId(postId);

        // 본인 소유 조건으로 삭제 시도
        int affected = postRepository.deleteByIdAndOwner(postId, userId);
        if (affected == 0) {
            // 존재하긴 하는데 내 소유가 아님
            throw new PostHandler(PostErrorStatus.FORBIDDEN_ACCESS);
        }
    }

    public PostAnalysisResponse getMyPostAnalysis(Long userId, Long postId) {
        Post post = postRepository.findByIdAndSong_User_Id(postId, userId)
                .orElseThrow(() -> new PostHandler(PostErrorStatus.FORBIDDEN_ACCESS));

        String title = post.getSong().getTitle();
        String artistName = post.getSong().getUser().getNickname() != null
                ? post.getSong().getUser().getNickname()
                : post.getSong().getUser().getName();

        return PostAnalysisResponse.builder()
                .postId(post.getId())
                .songTitle(title)
                .artistName(artistName)
                .shareSummary(post.getShareSummary())
                .shareDetails(post.getShareDetails())
                .keywords(post.getKeywords())
                .analyzedAt(toInstant(post.getUpdatedAt()))
                .build();
    }

    private static Instant toInstant(LocalDateTime ldt) {
        return (ldt == null) ? null : ldt.atOffset(ZoneOffset.ofHours(9)).toInstant();
    }
}
