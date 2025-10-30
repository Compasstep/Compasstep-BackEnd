package com.fifo.compasstep.post.domain;

import com.fasterxml.jackson.databind.JsonNode;
import com.fifo.compasstep.common.domain.BaseEntity;
import com.fifo.compasstep.guestComment.domain.GuestComment;
import com.fifo.compasstep.song.domain.Song;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "posts")
@Getter
@SuperBuilder
@ToString(exclude = "song")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Post extends BaseEntity {

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "song_id", nullable = false, unique = true)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Song song;

    @Column(name = "share_summary", columnDefinition = "jsonb")
    @JdbcTypeCode(SqlTypes.JSON)
    private JsonNode shareSummary;   // null 가능

    @Column(name = "share_details", columnDefinition = "jsonb")
    @JdbcTypeCode(SqlTypes.JSON)
    private JsonNode shareDetails;   // null 가능

    @Column(name = "post_name")
    private String postName;         // null 가능

    @Column(name = "keywords", columnDefinition = "jsonb")
    @JdbcTypeCode(SqlTypes.JSON)
    private JsonNode keywords;       // null 가능

    @Column(name = "analyzed", nullable = false)
    @Builder.Default
    private boolean analyzed = false;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<GuestComment> guestComments = new ArrayList<>();

    public static Post create(
            Song song, JsonNode shareSummary, JsonNode shareDetails, String postName, JsonNode keywords
    ) {
        Post post = Post.builder()
                .song(song)
                .shareSummary(shareSummary)
                .shareDetails(shareDetails)
                .postName(postName)
                .keywords(keywords)
                .analyzed(false)
                .build();
        song.setPost(post);
        return post;
    }
}
