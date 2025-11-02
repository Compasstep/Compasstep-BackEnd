package com.fifo.compasstep.retrainingData.domain;

import com.fifo.compasstep.common.domain.BaseEntity;
import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.Generated;
import org.hibernate.annotations.Type;
import org.hibernate.generator.EventType;

import java.util.List;

@Entity
@DynamicInsert
@Table(
        name = "retraining_data",
        indexes = @Index(name = "uq_comment_hash", columnList = "comment_hash", unique = true)
)
@Getter
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RetrainingData extends BaseEntity {

    @Column(name = "comment_text", nullable = false)
    private String commentText;

    @Column(
            name = "comment_hash",
            columnDefinition =
                    "char(32) generated always as (" +
                            "md5(lower(trim(regexp_replace(comment_text, '\\\\s+', ' ', 'g'))))" +
                            ") stored",
            nullable = false,
            insertable = false,
            updatable = false
    )

    @Generated(event = { EventType.INSERT, EventType.UPDATE })
    private String commentHash;

    @Type(JsonBinaryType.class)
    @Column(name = "prediction", columnDefinition = "jsonb")
    private List<String> prediction;

    @Column(name = "confidence")
    private Double confidence;

    @Column(name = "is_learned")
    private Boolean isLearned;

    @Column(name = "is_reviewed")
    private Boolean isReviewed;

    /* 도메인 메서드 (옵션) */
    public void replacePredictionLabels(List<String> labels) { this.prediction = labels; }
    public void markReviewed() { this.isReviewed = true; }
    public void markUnreviewed() { this.isReviewed = false; }
}
