// retrainingData/service/RetrainingDataService.java
package com.fifo.compasstep.retrainingData.service;

import com.fifo.compasstep.apipayload.exceptions.handler.RetrainingDataHandler;
import com.fifo.compasstep.retrainingData.domain.RetrainingData;
import com.fifo.compasstep.retrainingData.domain.RetrainingDataRepository;
import com.fifo.compasstep.retrainingData.dto.request.UpdateInvalidRequestDTO;
import com.fifo.compasstep.retrainingData.dto.response.RetrainingListDTO;
import com.fifo.compasstep.retrainingData.exceptions.RetrainingErrorStatus;
import com.fifo.compasstep.retrainingData.utils.RetrainingDataUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.PersistenceException;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RetrainingDataService {

    private final RetrainingDataRepository repo;

    /** 비정상 데이터 조회 */
    public RetrainingListDTO getInvalid(int page, int size) {
        validatePage(page, size);
        PageRequest pr = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<RetrainingData> p = repo.findByIsReviewedFalse(pr);

        var items = p.getContent().stream()
                .map(RetrainingDataUtils::toItemDtoInvalid)
                .toList();

        return RetrainingListDTO.builder()
                .pageMeta(RetrainingDataUtils.toPageMeta(p))
                .reviews(items)
                .build();
    }

    /** 정상 데이터 조회 */
    public RetrainingListDTO getValid(int page, int size) {
        validatePage(page, size);
        PageRequest pr = PageRequest.of(page, size,
                Sort.by(Sort.Direction.DESC, "updatedAt")
                        .and(Sort.by(Sort.Direction.DESC, "createdAt")));
        Page<RetrainingData> p = repo.findByIsReviewedTrue(pr);

        var items = p.getContent().stream()
                .map(RetrainingDataUtils::toItemDtoValid)
                .toList();

        return RetrainingListDTO.builder()
                .pageMeta(RetrainingDataUtils.toPageMeta(p))
                .reviews(items)
                .build();
    }

    /** 비정상 → 정상 전환 (라벨 교체 포함) */
    @Transactional
    public void convertInvalidToValid(Long id, UpdateInvalidRequestDTO req) {
        if (req == null || req.getFinalLabels() == null) {
            throw new RetrainingDataHandler(RetrainingErrorStatus.INVALID_REQUEST);
        }
        var normalized = req.getFinalLabels().stream()
                .map(s -> s == null ? "" : s.trim())
                .filter(s -> !s.isEmpty())
                .distinct()
                .toList();
        if (normalized.isEmpty()) {
            throw new RetrainingDataHandler(RetrainingErrorStatus.INVALID_LABELS);
        }

        var data = repo.findById(id)
                .orElseThrow(() -> new RetrainingDataHandler(RetrainingErrorStatus.NOT_FOUND));
        if (Boolean.TRUE.equals(data.getIsReviewed())) {
            throw new RetrainingDataHandler(RetrainingErrorStatus.ALREADY_REVIEWED);
        }

        try {
            data.replacePredictionLabels(normalized); // jsonb(List<String>) 변경
            data.markReviewed();                      // 상태 변경
            log.info("[Retrain] invalid -> valid. id={}, finalLabels={}", id, normalized);
        } catch (DataAccessException | PersistenceException e) {
            log.error("[Retrain] DB update error. id={}, labels={}", id, normalized, e);
            throw new RetrainingDataHandler(RetrainingErrorStatus.DB_ERROR);
        }
    }

    /** 정상 → 비정상 되돌리기 */
    @Transactional
    public void convertValidToInvalid(Long id) {
        var data = repo.findById(id)
                .orElseThrow(() -> new RetrainingDataHandler(RetrainingErrorStatus.NOT_FOUND));

        if (Boolean.FALSE.equals(data.getIsReviewed())) {
            throw new RetrainingDataHandler(RetrainingErrorStatus.NOT_REVIEWED_YET);
        }

        try {
            data.markUnreviewed(); // 변경감지로 반영
            log.info("[Retrain] valid -> invalid. id={}", id);
        } catch (DataAccessException | PersistenceException e) {
            log.error("[Retrain] DB update error (revert). id={}", id, e);
            throw new RetrainingDataHandler(RetrainingErrorStatus.DB_ERROR);
        }
    }

    /* ===== validators ===== */
    private void validatePage(int page, int size) {
        if (page < 0 || size <= 0 || size > 100) {
            throw new RetrainingDataHandler(RetrainingErrorStatus.INVALID_REQUEST);
        }
    }
}
