package com.fifo.compasstep.reputationAnalysis.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface ReputationAnalysisRepository extends JpaRepository<ReputationAnalysis, Long> {

    /* 목록 조회: userId 기준 */
    interface ListRow {
        Long getHistoryId();
        String getSongTitle();
        String getArtistName();
        Instant getCreatedAt();
    }

    @Query(value = """
            SELECT
              ra.id          AS historyId,
              ra.song_title  AS songTitle,
              ra.artist_name AS artistName,
              ra.created_at  AS createdAt
            FROM reputation_analysis ra
            WHERE ra.user_id = :userId
            ORDER BY ra.created_at DESC
            """, nativeQuery = true)
    List<ListRow> findListByUserId(@Param("userId") Long userId);

    /* 상세 조회: historyId 기준 */
    interface DetailRow {
        Long getHistoryId();
        String getSongTitle();
        String getArtistName();
        Instant getCreatedAt();
        String getSentimentSummary(); // jsonb text
        String getEmotionDetails();   // jsonb text
        String getKeywords();         // jsonb text
    }

    @Query(value = """
            SELECT
              ra.id                              AS historyId,
              ra.song_title                      AS songTitle,
              ra.artist_name                     AS artistName,
              ra.created_at                      AS createdAt,
              CAST(ra.sentiment_summary AS text) AS sentimentSummary,
              CAST(ra.emotion_details  AS text)  AS emotionDetails,
              CAST(ra.keywords         AS text)  AS keywords
            FROM reputation_analysis ra
            WHERE ra.id = :historyId
            """, nativeQuery = true)
    Optional<DetailRow> findDetailById(@Param("historyId") Long historyId);

    /* 소유자 확인: 존재하면 true */
    @Query(value = """
            SELECT EXISTS(
              SELECT 1
              FROM reputation_analysis ra
              WHERE ra.id = :historyId AND ra.user_id = :userId
            )
            """, nativeQuery = true)
    boolean existsByIdAndUserId(@Param("historyId") Long historyId, @Param("userId") Long userId);
}
