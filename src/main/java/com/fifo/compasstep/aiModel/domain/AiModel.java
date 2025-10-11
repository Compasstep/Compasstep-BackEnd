package com.fifo.compasstep.aiModel.domain;

import com.fifo.compasstep.common.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;


@Entity
@Table(name = "ai_model")
@Getter
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AiModel extends BaseEntity {
    @Column(name = "is_active", nullable = false)
    private Boolean isActive;

    @Column(name = "save_path", nullable = false)
    private String savePath;

    @Column(name = "f1_micro_score", nullable = false)
    private Float f1MicroScore;

    @Column(name = "f1_macro_score", nullable = false)
    private Float f1MacroScore;

    @Column(nullable = false)
    private Float accuracy;
}   
