package com.fifo.compasstep.retrainingData.utils;

import com.fifo.compasstep.retrainingData.domain.RetrainingData;
import com.fifo.compasstep.retrainingData.dto.response.PageMetaDTO;
import com.fifo.compasstep.retrainingData.dto.response.RetrainingItemDTO;
import org.springframework.data.domain.Page;

import java.time.ZoneOffset;


public class RetrainingDataUtils {

    private RetrainingDataUtils() {}


    /** 비정상 데이터용 아이템 DTO */
    public static RetrainingItemDTO toItemDtoInvalid(RetrainingData e) {
        return RetrainingItemDTO.builder()
                .reviewId(e.getId())
                .isLearned(e.getIsLearned())
                .commentText(e.getCommentText())
                .emotions(e.getPrediction()) // jsonb(List<String>) 그대로
                .confidence(e.getConfidence() == null ? null : Double.valueOf(e.getConfidence()))
                .createdAt(e.getCreatedAt().atZone(ZoneOffset.UTC))
                .updatedAt(null)
                .build();
    }

    /** 정상 데이터용 아이템 DTO */
    public static RetrainingItemDTO toItemDtoValid(RetrainingData e) {
        return RetrainingItemDTO.builder()
                .reviewId(e.getId())
                .isLearned(e.getIsLearned())
                .commentText(e.getCommentText())
                .emotions(e.getPrediction())
                .confidence(e.getConfidence() == null ? null : Double.valueOf(e.getConfidence()))
                .createdAt(e.getCreatedAt().atZone(ZoneOffset.UTC))
                .updatedAt(e.getUpdatedAt() == null ? null : e.getUpdatedAt().atZone(ZoneOffset.UTC))
                .build();
    }

    /** 페이지 메타 빌더 */
    public static PageMetaDTO toPageMeta(Page<?> p) {
        return PageMetaDTO.builder()
                .page(p.getNumber())
                .size(p.getSize())
                .totalPages(p.getTotalPages())
                .totalElements(p.getTotalElements())
                .hasNext(p.hasNext())
                .hasPrev(p.hasPrevious())
                .build();
    }
}
