// src/main/java/com/fifo/compasstep/chat/service/DiscoveryService.java
package com.fifo.compasstep.reference.service;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fifo.compasstep.apipayload.ApiResponse;
import com.fifo.compasstep.apipayload.exceptions.handler.ReferenceHandler; // ✅ 변경
import com.fifo.compasstep.reference.client.KeywordDiscoveryClient;
import com.fifo.compasstep.reference.dto.LyricsAnalysis.LyricsAnalysisResultDTO;
import com.fifo.compasstep.reference.dto.friend.FriendAnalysisResultDTO;
import com.fifo.compasstep.reference.dto.youtube.PeerAnalysisResultDTO;
import com.fifo.compasstep.reference.dto.response.TrackVideoDto;
import com.fifo.compasstep.reference.exceptions.DiscoveryErrorStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class DiscoveryService {

    private final KeywordDiscoveryClient client;

    /** FastAPI로 전달 후 우리 ApiResponse로 변환 */
    @SuppressWarnings("unchecked")
    public ApiResponse<List<TrackVideoDto>> discover(Long userId, String query) {
        Map<String, Object> res;
        try {
            res = client.discoveryByKeyword(userId, query);
        } catch (Exception e) {
            throw new ReferenceHandler(DiscoveryErrorStatus.FASTAPI_ERROR); // ✅ 변경
        }
        if (res == null) throw new ReferenceHandler(DiscoveryErrorStatus.FASTAPI_ERROR); // ✅ 변경

        String code = Objects.toString(res.get("code"), "");
        String message = Objects.toString(res.get("message"), "");
        Object resultObj = res.get("result");

        switch (code) {
            case "200" -> {
                List<TrackVideoDto> items = toTrackVideoList(resultObj);
                return new ApiResponse<>(200,
                        (message != null && !message.isBlank()) ? message : "성공",
                        items);
            }
            case "204" -> {
                return new ApiResponse<>(204,
                        (message != null && !message.isBlank()) ? message : "추천 결과가 없습니다.",
                        List.of());
            }
            case "422" -> throw new ReferenceHandler(DiscoveryErrorStatus.FASTAPI_UNPROCESSABLE);
            case "403" -> throw new ReferenceHandler(DiscoveryErrorStatus.USER_FORBIDDEN);
            case "404" -> throw new ReferenceHandler(DiscoveryErrorStatus.USER_NOT_FOUND);
            case "400" -> throw new ReferenceHandler(DiscoveryErrorStatus.INVALID_REQUEST);
            default -> throw new ReferenceHandler(DiscoveryErrorStatus.FASTAPI_ERROR);
        }
    }
    public ApiResponse<PeerAnalysisResultDTO> analyzeYoutube(String songTitle, String artistName, Long userId) {
        Map<String, Object> res;

        try {
            //새 클라이언트 메소드 호출
            res = client.analyzePeerReputation(songTitle, artistName, userId);
        } catch (Exception e) {
            throw new ReferenceHandler(DiscoveryErrorStatus.FASTAPI_ERROR);
        }
        if (res == null) throw new ReferenceHandler(DiscoveryErrorStatus.FASTAPI_ERROR);

        String code = Objects.toString(res.get("code"), "");
        String message = Objects.toString(res.get("message"), "");
        Object resultObj = res.get("result"); // 이 부분이 이제 Map 형태일 것입니다.

        switch (code) {
            case "200" -> {
                // 핵심: toTrackVideoList 대신 새 파싱 메소드 호출
                PeerAnalysisResultDTO resultDto = toPeerAnalysisResult(resultObj);
                return new ApiResponse<>(200,
                        (message != null && !message.isBlank()) ? message : "성공",
                        resultDto); // DTO를 result에 담아 반환
            }
            case "204" -> {
                return new ApiResponse<>(204,
                        (message != null && !message.isBlank()) ? message : "분석 결과가 없습니다.",
                        null); // 204일 때는 result가 null일 수 있습니다.
            }
            case "422" -> throw new ReferenceHandler(DiscoveryErrorStatus.FASTAPI_UNPROCESSABLE);
            case "403" -> throw new ReferenceHandler(DiscoveryErrorStatus.USER_FORBIDDEN);
            case "404" -> throw new ReferenceHandler(DiscoveryErrorStatus.USER_NOT_FOUND);
            case "400" -> throw new ReferenceHandler(DiscoveryErrorStatus.INVALID_REQUEST);
            default -> throw new ReferenceHandler(DiscoveryErrorStatus.FASTAPI_ERROR);
        }
    }

    /**
     * 지인 평가 분석을 요청하고, ApiResponse로 변환하여 반환합니다.
     * @param postId 프론트엔드에서 받은 게시물 ID
     * @return ApiResponse<FriendAnalysisResultDto>
     */
    public ApiResponse<FriendAnalysisResultDTO> analyzeFriend(Long postId) {
        Map<String, Object> res;
        try {
            // 2번에서 만든 새 클라이언트 메소드 호출
            res = client.analyzeFriendReputation(postId);
        } catch (Exception e) {
            throw new ReferenceHandler(DiscoveryErrorStatus.FASTAPI_ERROR);
        }
        if (res == null) throw new ReferenceHandler(DiscoveryErrorStatus.FASTAPI_ERROR);

        String code = Objects.toString(res.get("code"), "");
        String message = Objects.toString(res.get("message"), "");
        Object resultObj = res.get("result"); // 이 부분이 Map 형태일 것입니다.

        switch (code) {
            case "200" -> {
                // ObjectMapper를 사용해 DTO로 변환
                FriendAnalysisResultDTO resultDto = toFriendAnalysisResult(resultObj);
                return new ApiResponse<>(200,
                        (message != null && !message.isBlank()) ? message : "성공",
                        resultDto); // 새 DTO를 result에 담아 반환
            }
            case "204" -> {
                return new ApiResponse<>(204,
                        (message != null && !message.isBlank()) ? message : "분석 결과가 없습니다.",
                        null);
            }
            // 이전에 정의한 다른 에러 케이스들...
            case "422" -> throw new ReferenceHandler(DiscoveryErrorStatus.FASTAPI_UNPROCESSABLE);
            case "404" -> throw new ReferenceHandler(DiscoveryErrorStatus.USER_NOT_FOUND);
            // ... (기타 4xx 코드)
            default -> throw new ReferenceHandler(DiscoveryErrorStatus.FASTAPI_ERROR);
        }
    }
    /**
     * 가사 감정분석 및 보컬 코칭을 요청하고, ApiResponse로 변환하여 반환합니다.
     * @param lyricsId 프론트엔드에서 받은 가사 ID
     * @return ApiResponse<LyricsAnalysisResultDto>
     */
    public ApiResponse<LyricsAnalysisResultDTO> analyzeLyrics(Long lyricsId) {
        Map<String, Object> res;
        try {
            // 2번에서 만든 새 클라이언트 메소드 호출
            res = client.analyzeLyrics(lyricsId);
        } catch (Exception e) {
            throw new ReferenceHandler(DiscoveryErrorStatus.FASTAPI_ERROR);
        }
        if (res == null) throw new ReferenceHandler(DiscoveryErrorStatus.FASTAPI_ERROR);

        String code = Objects.toString(res.get("code"), "");
        String message = Objects.toString(res.get("message"), "");
        Object resultObj = res.get("result"); // 이 부분이 Map 형태일 것입니다.

        switch (code) {
            case "200" -> {
                // ObjectMapper를 사용해 DTO로 변환
                LyricsAnalysisResultDTO resultDto = toLyricsAnalysisResult(resultObj);
                return new ApiResponse<>(200,
                        (message != null && !message.isBlank()) ? message : "성공",
                        resultDto); // 새 DTO를 result에 담아 반환
            }
            case "204" -> {
                return new ApiResponse<>(204,
                        (message != null && !message.isBlank()) ? message : "분석 결과가 없습니다.",
                        null);
            }
            // 이전에 정의한 다른 에러 케이스들...
            case "422" -> throw new ReferenceHandler(DiscoveryErrorStatus.FASTAPI_UNPROCESSABLE);
            case "404" -> throw new ReferenceHandler(DiscoveryErrorStatus.USER_NOT_FOUND);
            // ... (기타 4xx 코드)
            default -> throw new ReferenceHandler(DiscoveryErrorStatus.FASTAPI_ERROR);
        }
    }

    /**
     * Map(Object)을 LyricsAnalysisResultDto로 변환합니다.
     * @param resultObj FastAPI 응답의 result 필드
     * @return LyricsAnalysisResultDto
     */
    private LyricsAnalysisResultDTO toLyricsAnalysisResult(Object resultObj) {
        if (resultObj == null) {
            return null;
        }

        try {
            // ObjectMapper가 Map을 DTO 클래스로 자동 변환해줍니다.
            return objectMapper.convertValue(resultObj, LyricsAnalysisResultDTO.class);
        } catch (IllegalArgumentException e) {
            // 변환 실패 시 (구조가 맞지 않을 때)
            // log.error("FastAPI (Lyrics) result DTO 변환 실패", e);
            throw new ReferenceHandler(DiscoveryErrorStatus.FASTAPI_ERROR);
        }
    }



    @SuppressWarnings("unchecked")
    private List<TrackVideoDto> toTrackVideoList(Object resultObj) {
        if (!(resultObj instanceof List<?> raw)) return List.of();
        List<TrackVideoDto> out = new ArrayList<>();
        for (Object o : raw) {
            if (o instanceof Map<?,?> m) {
                out.add(TrackVideoDto.builder()
                        .videoId(Objects.toString(m.get("videoId"), ""))
                        .title(Objects.toString(m.get("title"), ""))
                        .channelName(Objects.toString(m.get("channelName"), ""))
                        .thumbnailUrl(Objects.toString(m.get("thumbnailUrl"), ""))
                        .youtubeUrl(Objects.toString(m.get("youtubeUrl"), ""))
                        .build());
            } else if (o instanceof String s) {
                out.add(TrackVideoDto.builder()
                        .videoId("").title(s).channelName("")
                        .thumbnailUrl("").youtubeUrl("").build());
            }
        }
        return out;
    }

     private final ObjectMapper objectMapper = new ObjectMapper()
        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false); // 모르는 필드는 무시
    private PeerAnalysisResultDTO toPeerAnalysisResult(Object resultObj) {
        if (resultObj == null) {
            return null;
        }

        try {
            // ObjectMapper가 Map을 DTO 클래스로 자동 변환해줍니다.
            return objectMapper.convertValue(resultObj, PeerAnalysisResultDTO.class);
        } catch (IllegalArgumentException e) {
            // 타입 변환 실패 시 예외 처리
            throw new ReferenceHandler(DiscoveryErrorStatus.FASTAPI_ERROR); // 혹은 적절한 다른 에러
        }
    }

    /**
     * Map(Object)을 FriendAnalysisResultDto로 변환합니다.
     * @param resultObj FastAPI 응답의 result 필드
     * @return FriendAnalysisResultDto
     */
    private FriendAnalysisResultDTO toFriendAnalysisResult(Object resultObj) {
        if (resultObj == null) {
            return null;
        }

        try {
            // ObjectMapper가 Map을 DRequestDto 클래스로 자동 변환해줍니다.
            return objectMapper.convertValue(resultObj, FriendAnalysisResultDTO.class);
        } catch (IllegalArgumentException e) {
            // 변환 실패 시 (구조가 맞지 않을 때)
            // log.error("FastAPI (Friend) result DTO 변환 실패", e);
            throw new ReferenceHandler(DiscoveryErrorStatus.FASTAPI_ERROR);
        }
    }

}
