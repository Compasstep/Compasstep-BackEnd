package com.fifo.compasstep.retrainingData.domain;

import com.fifo.compasstep.common.domain.BaseEntity;
import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.Type;

import java.util.List;

@Entity
@Table(name = "retraining_data") // <- jakarta.persistence.Table
@Getter
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DynamicUpdate
public class RetrainingData extends BaseEntity {

    @Column(name = "comment_text", nullable = false)
    private String commentText;

    // jsonb 매핑
    @Type(JsonBinaryType.class) // <- org.hibernate.annotations.Type
    @Column(name = "prediction", columnDefinition = "jsonb", nullable = false)
    private List<String> prediction;

    @Column(name = "is_reviewed", nullable = false)
    @ColumnDefault("false")
    private Boolean isReviewed;

    @Column(nullable = false)
    private Float confidence;

    @Column(name = "is_learned", nullable = false)
    private Boolean isLearned;

    /* 도메인 메서드 */
    public void replacePredictionLabels(List<String> labels) { this.prediction = labels; }
    public void markReviewed() { this.isReviewed = true; }
    public void markUnreviewed() { this.isReviewed = false; }
}
