// src/main/java/com/fifo/compasstep/lyrics_analysis/exceptions/LyricsAnalysisErrorStatus.java
package com.fifo.compasstep.lyrics_analysis.exceptions;

import com.fifo.compasstep.apipayload.code.BaseErrorCode;
import com.fifo.compasstep.apipayload.code.ErrorReasonDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum LyricsAnalysisErrorStatus implements BaseErrorCode {

    // 400
    INVALID_REQUEST(HttpStatus.BAD_REQUEST, 400, "잘못된 요청입니다."),

    // 403 (선택: 소유자/권한 검증 시 사용)
    FORBIDDEN_ACCESS(HttpStatus.FORBIDDEN, 403, "해당 리소스에 대한 권한이 없습니다."),

    // 404
    ANALYSIS_NOT_FOUND(HttpStatus.NOT_FOUND, 404, "가사 분석을 찾을 수 없습니다."),

    // 500
    JSON_PARSE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, 500, "분석 결과를 파싱하는 중 오류가 발생했습니다."),
    DB_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, 500, "데이터 처리 중 오류가 발생했습니다.");

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
