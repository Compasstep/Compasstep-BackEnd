// src/main/java/com/fifo/compasstep/reference/service/GenreRankingService.java
package com.fifo.compasstep.reference.service;

import com.fifo.compasstep.apipayload.exceptions.GeneralException;
import com.fifo.compasstep.reference.client.SpotifyClient;
import com.fifo.compasstep.reference.dto.RankingItemDto;
import com.fifo.compasstep.reference.dto.ReferenceRequestDto;
import com.fifo.compasstep.reference.exceptions.ReferenceErrorStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class GenreRankingService {

    private final SpotifyClient spotifyClient;

    /** Spotify 장르 기반 인기 트랙 */
    public List<RankingItemDto> getGenreRanking(ReferenceRequestDto req) {
        try {
            String token = spotifyClient.getAccessToken();

            Map<String, Object> json = spotifyClient.searchTracksByGenre(
                    req.genre(), req.marketOrDefault(), req.limitOrDefault(), 0, token
            );

            List<Map<String, Object>> items = extractTrackItems(json);

            // popularity DESC 정렬 → limit → rank 매김
            List<Map<String, Object>> sorted = items.stream()
                    .sorted((a, b) -> Integer.compare(
                            getInt(b), getInt(a)
                    ))
                    .limit(req.limitOrDefault())
                    .toList();

            List<RankingItemDto> result = new ArrayList<>();
            int rank = 1;
            for (Map<String, Object> t : sorted) {
                result.add(toRankingItem(rank++, t));
            }
            return result;

        } catch (GeneralException ge) {
            throw ge;
        } catch (Exception e) {
            throw new GeneralException(ReferenceErrorStatus.EXTERNAL_API_ERROR);
        }
    }

    @SuppressWarnings("unchecked")
    private List<Map<String, Object>> extractTrackItems(Map<String, Object> json) {
        if (json == null) return List.of();
        Map<String, Object> tracks = (Map<String, Object>) json.get("tracks");
        if (tracks == null) return List.of();
        return (List<Map<String, Object>>) tracks.getOrDefault("items", List.of());
    }

    private RankingItemDto toRankingItem(int rank, Map<String, Object> t) {
        String title = Objects.toString(t.get("name"), "");
        String artistName = extractFirstArtistName(t);
        String imageUrl = extractAlbumImageUrl(t); // ★ Spotify album.images 사용
        return new RankingItemDto(rank, title, artistName, imageUrl);
    }

    @SuppressWarnings("unchecked")
    private String extractFirstArtistName(Map<String, Object> t) {
        List<Map<String, Object>> artists =
                (List<Map<String, Object>>) t.getOrDefault("artists", List.of());
        if (artists.isEmpty()) return "";
        return Objects.toString(artists.get(0).get("name"), "");
    }

    @SuppressWarnings("unchecked")
    private String extractAlbumImageUrl(Map<String, Object> t) {
        Map<String, Object> album = (Map<String, Object>) t.getOrDefault("album", Map.of());
        List<Map<String, Object>> images =
                (List<Map<String, Object>>) album.getOrDefault("images", List.of());
        // Spotify는 보통 [0]=largest, [1]=medium(~300px), [2]=small
        return images.isEmpty() ? "" : Objects.toString(images.get(0).get("url"), "");
    }

    private int getInt(Map<String, Object> map) {
        Object v = map.getOrDefault("popularity", 0);
        if (v instanceof Number n) return n.intValue();
        try {
            return Integer.parseInt(String.valueOf(v));
        } catch (Exception ignored) {
            return 0;
        }
    }
}
