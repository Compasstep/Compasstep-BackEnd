package com.fifo.compasstep.song.domain;

import com.fifo.compasstep.common.domain.BaseEntity;
import com.fifo.compasstep.post.domain.Post;
import com.fifo.compasstep.user.domain.User;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "song")
@Getter
@ToString(exclude = "user")
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Song extends BaseEntity {
    //userPKId
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, name = "s3_file_key")
    private String s3FileKey;

    // Post와의 1:1 양방향 관계를 위해 추가
    @OneToOne(mappedBy = "song", cascade = CascadeType.ALL, orphanRemoval = true)
    private Post post;

    // Post에서 관계를 설정할 때 사용할 package-private setter
    public void setPost(Post post) {
        this.post = post;
    }

    // --- 정적 팩토리 메소드 ---
    public static Song create(User user, String title, String s3FileKey) {
        // 1. 빌더를 사용해 Song 객체 생성
        Song song = Song.builder()
                .title(title)
                .s3FileKey(s3FileKey)
                .build();

        // 2. 생성 시점에 양방향 관계를 완벽하게 설정
        song.user = user;
        user.getSongs().add(song);

        // 3. 완벽한 상태의 객체 반환
        return song;
    }
}
