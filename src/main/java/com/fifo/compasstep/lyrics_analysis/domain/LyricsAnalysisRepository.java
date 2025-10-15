package com.fifo.compasstep.lyrics_analysis.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface LyricsAnalysisRepository extends JpaRepository<LyricsAnalysis, Long> {

    /*  목록 조회 (userId 기준) */
    interface ListRow {
        Long getLyricsAnalysisId();
        String getLyricsTitle();
        Instant getCreatedAt();
    }

    @Query(value = """
            SELECT
                la.id AS lyricsAnalysisId,
                l.title AS lyricsTitle,
                la.created_at AS createdAt
            FROM lyrics_analysis la
            JOIN lyrics l ON la.lyrics_id = l.id
            WHERE l.user_id = :userId
            ORDER BY la.created_at DESC
            """, nativeQuery = true)
    List<ListRow> findListByUserId(@Param("userId") Long userId);

    /*  상세 조회 (analysisId 기준) */
    interface DetailRow {
        Long getLyricsAnalysisId();
        String getLyricsTitle();
        Instant getCreatedAt();
        String getAnalysisResult(); // jsonb 문자열
    }

    @Query(value = """
            SELECT
                la.id AS lyricsAnalysisId,
                l.title AS lyricsTitle,
                la.created_at AS createdAt,
                cast(la.analysis_result as text) AS analysisResult
            FROM lyrics_analysis la
            JOIN lyrics l ON la.lyrics_id = l.id
            WHERE la.id = :analysisId
            """, nativeQuery = true)
    Optional<DetailRow> findDetailById(@Param("analysisId") Long analysisId);

    /*  권한/소유자 확인 */
    @Query(value = """
    SELECT NOT EXISTS (
      SELECT 1
      FROM lyrics_analysis la
      JOIN lyrics l ON la.lyrics_id = l.id
      WHERE la.id = :analysisId AND l.user_id = :userId
    )
    """, nativeQuery = true)
    boolean notExistsByIdAndUserId(@Param("analysisId") Long analysisId, @Param("userId") Long userId);

}