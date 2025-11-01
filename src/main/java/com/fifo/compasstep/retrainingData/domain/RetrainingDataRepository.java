// retrainingData/domain/RetrainingDataRepository.java
package com.fifo.compasstep.retrainingData.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RetrainingDataRepository extends JpaRepository<RetrainingData, Long> {
    Page<RetrainingData> findByIsReviewedFalse(Pageable pageable);
    Page<RetrainingData> findByIsReviewedTrue(Pageable pageable);
}
