package com.fifo.compasstep.lyrics_analysis.domain;

import com.fifo.compasstep.common.domain.BaseEntity;
import com.fifo.compasstep.lyrics.domain.Lyrics;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "lyrics_analysis")
@Getter
@SuperBuilder
@ToString(exclude = "lyrics")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class LyricsAnalysis extends BaseEntity {
    //lyricsPKId
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lyrics_id", nullable = false, unique = true)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Lyrics lyrics;

    @Column(name = "analysis_result", nullable = false, columnDefinition = "jsonb")
    private String analysisResult;

    // 4. 정적 팩토리 메소드 추가
    public static LyricsAnalysis create(Lyrics lyrics, String analysisResult) {
        LyricsAnalysis lyricsAnalysis = LyricsAnalysis.builder()
                .lyrics(lyrics)
                .analysisResult(analysisResult)
                .build();
        lyrics.setLyricsAnalysis(lyricsAnalysis); // 양방향 관계 설정
        return lyricsAnalysis;
    }
}
