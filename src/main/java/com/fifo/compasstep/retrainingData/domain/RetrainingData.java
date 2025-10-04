package com.fifo.compasstep.retrainingData.domain;

import com.fifo.compasstep.common.domain.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.ColumnDefault;

@Entity
@Table(name = "retraining_data")
@Getter
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RetrainingData extends BaseEntity {

    @Column(name = "comment_text", nullable = false)
    private String commentText;

    @Column(nullable = false, columnDefinition = "jsonb")
    private String prediction;

    @Column(name = "is_reviewed", nullable = false)
    @ColumnDefault("false")
    private Boolean isReviewed;

    @Column(nullable = false)
    private Float confidence;

    @Column(name = "is_learned", nullable = false)
    private Boolean isLearned;

}
