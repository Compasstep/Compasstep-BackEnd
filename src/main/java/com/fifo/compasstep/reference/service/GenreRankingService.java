// src/main/java/com/fifo/compasstep/reference/service/GenreRankingService.java
package com.fifo.compasstep.reference.service;

import com.fifo.compasstep.apipayload.exceptions.handler.ReferenceHandler;
import com.fifo.compasstep.reference.client.SpotifyClient;
import com.fifo.compasstep.reference.dto.RankingItemDTO;
import com.fifo.compasstep.reference.dto.ReferenceRequestDTO;
import com.fifo.compasstep.reference.exceptions.ReferenceErrorStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class GenreRankingService {

    private static final int DEFAULT_OFFSET = 0;

    private final SpotifyClient spotifyClient;

    /** Spotify 장르 기반 인기 트랙 */
    @Transactional(readOnly = true)
    public List<RankingItemDTO> getGenreRanking(ReferenceRequestDTO req) {

        // 1) 입력 검증 (서비스 주도)
        if (req == null) {
            log.warn("[GenreRanking] null request");
            throw new ReferenceHandler(ReferenceErrorStatus.INVALID_REQUEST);
        }
        final String genre = trimToNull(req.genre());
        if (genre == null) {
            log.warn("[GenreRanking] missing genre param. req={}", req);
            throw new ReferenceHandler(ReferenceErrorStatus.GENRE_PARAM_MISSING);
        }
        final int limit = req.limitOrDefault();
        if (limit <= 0) {
            log.warn("[GenreRanking] invalid limit. limit={}", limit);
            throw new ReferenceHandler(ReferenceErrorStatus.LIMIT_PARAM_INVALID);
        }

        try {
            // 2) 토큰 발급 + 검색 호출
            final String token = spotifyClient.getAccessToken();
            final Map<String, Object> json = spotifyClient.searchTracksByGenre(
                    genre, req.marketOrDefault(), limit, DEFAULT_OFFSET, token
            );

            // 3) 아이템 추출 → popularity DESC → limit → rank 부여
            final List<Map<String, Object>> items = extractTrackItems(json);
            if (items.isEmpty()) {
                log.info("[GenreRanking] no items from Spotify. genre={}, market={}, limit={}",
                        genre, req.marketOrDefault(), limit);
            }

            final List<Map<String, Object>> sorted = items.stream()
                    .sorted((a, b) -> Integer.compare(getPopularity(b), getPopularity(a)))
                    .limit(limit)
                    .toList();

            final List<RankingItemDTO> result = new ArrayList<>(sorted.size());
            int rank = 1;
            for (Map<String, Object> t : sorted) {
                result.add(toRankingItem(rank++, t));
            }
            return result;

        } catch (ReferenceHandler rh) {
            // 도메인 커스텀 예외는 그대로 전파
            throw rh;

        } catch (RuntimeException re) {
            // 외부 API/파싱 등 런타임 예외: 통일 변환 + 구체 로그
            log.error("[GenreRanking] runtime error while calling Spotify. genre={}, market={}, limit={}",
                    genre, req.marketOrDefault(), limit, re);
            throw new ReferenceHandler(ReferenceErrorStatus.EXTERNAL_API_ERROR);

        } catch (Exception e) {
            // checked 예외도 동일 정책
            log.error("[GenreRanking] unexpected error. genre={}, market={}, limit={}",
                    genre, req.marketOrDefault(), limit, e);
            throw new ReferenceHandler(ReferenceErrorStatus.EXTERNAL_API_ERROR);
        }
    }

    // ===== 내부 유틸 =====

    @SuppressWarnings("unchecked")
    private List<Map<String, Object>> extractTrackItems(Map<String, Object> json) {
        if (json == null) return List.of();
        Map<String, Object> tracks = (Map<String, Object>) json.get("tracks");
        if (tracks == null) return List.of();
        Object raw = tracks.get("items");
        if (!(raw instanceof List<?> list)) return List.of();
        return (List<Map<String, Object>>) list;
    }

    private RankingItemDTO toRankingItem(int rank, Map<String, Object> t) {
        final String title = Objects.toString(t.get("name"), "");
        final String artistName = extractFirstArtistName(t);
        final String imageUrl = extractAlbumImageUrl(t); // Spotify album.images
        return new RankingItemDTO(rank, title, artistName, imageUrl);
    }

    @SuppressWarnings("unchecked")
    private String extractFirstArtistName(Map<String, Object> t) {
        Object raw = t.get("artists");
        if (!(raw instanceof List<?> list) || list.isEmpty()) return "";
        Map<String, Object> first = (Map<String, Object>) list.get(0);
        return Objects.toString(first.get("name"), "");
    }

    @SuppressWarnings("unchecked")
    private String extractAlbumImageUrl(Map<String, Object> t) {
        Object albumObj = t.get("album");
        if (!(albumObj instanceof Map<?,?> album)) return "";
        Object imagesObj = album.get("images");
        if (!(imagesObj instanceof List<?> list) || list.isEmpty()) return "";
        Map<String, Object> first = (Map<String, Object>) list.get(0); // 보통 [0]이 largest
        return Objects.toString(first.get("url"), "");
    }

    private int getPopularity(Map<String, Object> map) {
        Object v = map.getOrDefault("popularity", 0);
        if (v instanceof Number n) return n.intValue();
        try { return Integer.parseInt(String.valueOf(v)); }
        catch (Exception ignored) { return 0; }
    }

    private String trimToNull(String s) {
        if (s == null) return null;
        String t = s.trim();
        return t.isEmpty() ? null : t;
    }
}
