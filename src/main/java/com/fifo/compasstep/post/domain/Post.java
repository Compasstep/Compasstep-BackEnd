package com.fifo.compasstep.post.domain;

import com.fifo.compasstep.common.domain.BaseEntity;
import com.fifo.compasstep.guestComment.domain.GuestComment;
import com.fifo.compasstep.song.domain.Song;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "posts")
@Getter
@SuperBuilder
@ToString(exclude = "song")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Post extends BaseEntity {
    //songPKId
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "song_id", nullable = false, unique = true)
    private Song song;

    @Column(name = "share_link", nullable = false, unique = true)
    private String shareLink;

    @Column(name = "share_summary", nullable = false, columnDefinition = "jsonb")
    private String shareSummary;

    @Column(name = "share_details", nullable = false, columnDefinition = "jsonb")
    private String shareDetails;

    @Column(name = "post_name")
    private String postName;

    @Column(columnDefinition = "jsonb")
    private String keywords;

    // GuestComment와의 1:N 양방향 관계를 위해 추가
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default // 빌더 사용 시 NPE 방지를 위해 초기화
    private List<GuestComment> guestComments = new ArrayList<>();

    public static Post create(Song song, String shareLink, String shareSummary, String shareDetails, String postName, String keywords) {
        Post post = Post.builder()
                .song(song)
                .shareLink(shareLink)
                .shareSummary(shareSummary)
                .shareDetails(shareDetails)
                .postName(postName)
                .keywords(keywords)
                .build();

        song.setPost(post);
        return post;
    }
}
