package com.fifo.compasstep.lyrics_analysis.domain;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LyricsAnalysisRepository extends JpaRepository<LyricsAnalysis, Long> {

    // 목록: 특정 사용자(userId) 기준 최신순
    Page<LyricsAnalysis> findByLyrics_UserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);

    // 목록: 페이징 없이 전부 (필요 시)
    List<LyricsAnalysis> findByLyrics_UserIdOrderByCreatedAtDesc(Long userId);

    // 상세: 기본 제공 findById(Long) 써도 되지만, 명시적으로 두고 싶으면 아래 유지
    // Optional<LyricsAnalysis> findById(Long id);

    // 권한/소유자 확인
    boolean existsByIdAndLyrics_UserId(Long id, Long userId);

    // notExists 형태가 필요하면 기본 메서드로 제공
    default boolean notExistsByIdAndUserId(Long analysisId, Long userId) {
        return !existsByIdAndLyrics_UserId(analysisId, userId);
    }
}
