package com.fifo.compasstep.lyrics.domain;

import com.fifo.compasstep.common.domain.BaseEntity;
import com.fifo.compasstep.lyrics_analysis.domain.LyricsAnalysis;
import com.fifo.compasstep.user.domain.User;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "lyrics")
@Getter
@ToString(exclude = "user")
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Lyrics extends BaseEntity {
    //userPKId
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User user;

    @Column(nullable = false)
    private String title;

    @Column(name = "s3_lyrics", nullable = false)
    private String s3Lyrics;

    @OneToOne(mappedBy = "lyrics", cascade = CascadeType.ALL, orphanRemoval = true)
    private LyricsAnalysis lyricsAnalysis;

    // LyricsAnalysis에서 관계를 설정할 때 사용할 public setter
    public void setLyricsAnalysis(LyricsAnalysis lyricsAnalysis) {
        this.lyricsAnalysis = lyricsAnalysis;
    }

    public static Lyrics create(User user, String title, String s3Lyrics) {
        Lyrics lyrics = Lyrics.builder()
                .user(user)
                .title(title)
                .s3Lyrics(s3Lyrics)
                .build();

        user.getLyricsList().add(lyrics);
        return lyrics;
    }
}
