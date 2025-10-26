// src/main/java/com/fifo/compasstep/reputationAnalysis/domain/ReputationAnalysisRepository.java
package com.fifo.compasstep.reputationAnalysis.domain;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReputationAnalysisRepository extends JpaRepository<ReputationAnalysis, Long> {

    // 목록: 특정 사용자(userId) 기준 최신순 (페이징)
    Page<ReputationAnalysis> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);

    // 목록: 페이징 없이 전부
    List<ReputationAnalysis> findByUserIdOrderByCreatedAtDesc(Long userId);

    // 소유자 확인 (존재 여부)
    boolean existsByIdAndUserId(Long id, Long userId);

    // notExists 형태 필요 시 기본 메서드로 제공
    default boolean notExistsByIdAndUserId(Long historyId, Long userId) {
        return !existsByIdAndUserId(historyId, userId);
    }
}
