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
    INVALID_REQUEST(HttpStatus.BAD_REQUEST, 400, "잘못된 요청입니다."),

    // 404 (선택)
    YOUTUBE_VIDEO_NOT_FOUND(HttpStatus.NOT_FOUND, 404, "해당 곡의 유튜브 영상을 찾을 수 없습니다."),

    // 500
    EXTERNAL_API_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, 500, "랭킹 정보를 불러오는 중 오류가 발생했습니다."),
    SPOTIFY_AUTH_FAIL(HttpStatus.INTERNAL_SERVER_ERROR, 500, "외부 API 인증에 실패했습니다."),
    SPOTIFY_SEARCH_FAIL(HttpStatus.INTERNAL_SERVER_ERROR, 500, "외부 API 검색 호출에 실패했습니다.");

    private final HttpStatus status;
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
                .httpStatus(status)
                .build();
    }
}
