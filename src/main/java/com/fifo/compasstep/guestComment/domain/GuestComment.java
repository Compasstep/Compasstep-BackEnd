package com.fifo.compasstep.guestComment.domain;

import com.fifo.compasstep.common.domain.BaseEntity;
import com.fifo.compasstep.post.domain.Post;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "guest_comments")
@ToString(exclude = "post")
@Getter
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GuestComment extends BaseEntity {
    //postPKId
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Post post;

    @Column(nullable = false)
    private String comment;

    @Column(nullable = false)
    private Integer rate;

    public static GuestComment create(Post post, String comment, Integer rate) {
        GuestComment guestComment = GuestComment.builder()
                .comment(comment)
                .rate(rate)
                .build();

        post.getGuestComments().add(guestComment);
        guestComment.post = post;

        return guestComment;
    }

}
