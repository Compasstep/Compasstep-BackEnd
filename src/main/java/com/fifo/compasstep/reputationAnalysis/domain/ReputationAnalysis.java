package com.fifo.compasstep.reputationAnalysis.domain;

import com.fifo.compasstep.common.domain.BaseEntity;
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
@Table(name = "reputation_analysis")
@Getter
@ToString(exclude = "user")
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReputationAnalysis extends BaseEntity {
    //userPKId
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User user;

    @Column(name = "song_title", nullable = false)
    private String songTitle;

    @Column(name = "artist_name", nullable = false)
    private String artistName;

    @Column(name = "sentiment_summary", nullable = false, columnDefinition = "jsonb")
    private String sentimentSummary;

    @Column(name = "emotion_details", nullable = false, columnDefinition = "jsonb")
    private String emotionDetails;

    @Column(nullable = false, columnDefinition = "jsonb")
    private String keywords;

    public static ReputationAnalysis create(User user, String songTitle, String artistName, String sentimentSummary, String emotionDetails, String keywords) {
        ReputationAnalysis analysis = ReputationAnalysis.builder()
                .user(user)
                .songTitle(songTitle)
                .artistName(artistName)
                .sentimentSummary(sentimentSummary)
                .emotionDetails(emotionDetails)
                .keywords(keywords)
                .build();

        user.getReputationAnalysisList().add(analysis);
        return analysis;
    }
}
