package com.fifo.compasstep.reference.exceptions;

import com.fifo.compasstep.apipayload.code.BaseErrorCode;
import com.fifo.compasstep.apipayload.code.ErrorReasonDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ReferenceErrorStatus implements BaseErrorCode {

    // 400
    GENRE_PARAM_MISSING(HttpStatus.BAD_REQUEST, 400, "장르를 지정해주세요."),
    TITLE_PARAM_MISSING(HttpStatus.BAD_REQUEST, 400, "곡 제목을 지정해주세요."),
    ARTIST_PARAM_MISSING(HttpStatus.BAD_REQUEST, 400, "아티스트를 지정해주세요."),
    INVALID_REQUEST(HttpStatus.BAD_REQUEST, 400, "잘못된 요청입니다."),
    LIMIT_PARAM_INVALID(HttpStatus.BAD_REQUEST, 400, "limit 값이 유효하지 않습니다."),           // 선택
    MARKET_PARAM_INVALID(HttpStatus.BAD_REQUEST, 400, "market 값이 유효하지 않습니다."),        // 선택

    // 404
    YOUTUBE_VIDEO_NOT_FOUND(HttpStatus.NOT_FOUND, 404, "해당 곡의 유튜브 영상을 찾을 수 없습니다."),

    // 4xx/5xx (외부 연동)
    TOO_MANY_REQUESTS(HttpStatus.TOO_MANY_REQUESTS, 429, "요청이 너무 많습니다. 잠시 후 다시 시도해주세요."), // 선택
    EXTERNAL_API_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, 500, "외부 API 호출 중 오류가 발생했습니다."),
    SPOTIFY_AUTH_FAIL(HttpStatus.BAD_GATEWAY, 502, "외부 API 인증에 실패했습니다."),
    SPOTIFY_SEARCH_FAIL(HttpStatus.BAD_GATEWAY, 502, "외부 API 검색 호출에 실패했습니다."),
    UPSTREAM_UNAVAILABLE(HttpStatus.SERVICE_UNAVAILABLE, 503, "외부 서비스가 일시적으로 응답하지 않습니다."); // 선택

    private final HttpStatus httpStatus;   // ← 이름 통일
    private final int code;
    private final String message;

    @Override
    public ErrorReasonDTO getReason() {
        return ErrorReasonDTO.builder()
                .message(message)
                .code(code)
                .build();
    }

    @Override
    public ErrorReasonDTO getReasonHttpStatus() {
        return ErrorReasonDTO.builder()
                .message(message)
                .code(code)
                .httpStatus(httpStatus)
                .build();
    }
}
