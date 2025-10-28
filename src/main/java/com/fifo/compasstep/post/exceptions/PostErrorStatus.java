// src/main/java/com/fifo/compasstep/post/exceptions/PostErrorStatus.java
package com.fifo.compasstep.post.exceptions;

import com.fifo.compasstep.apipayload.code.BaseErrorCode;
import com.fifo.compasstep.apipayload.code.ErrorReasonDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum PostErrorStatus implements BaseErrorCode {

    // 400
    INVALID_REQUEST(HttpStatus.BAD_REQUEST, 400, "잘못된 요청입니다."),

    // 403
    FORBIDDEN_ACCESS(HttpStatus.FORBIDDEN, 403, "접근 권한이 없습니다."),

    // 404
    SONG_NOT_FOUND(HttpStatus.NOT_FOUND, 404, "노래를 찾을 수 없습니다."),
    POST_NOT_FOUND(HttpStatus.NOT_FOUND, 404, "게시글을 찾을 수 없습니다."),

    // 409
    DUPLICATE_POST_FOR_SONG(HttpStatus.CONFLICT, 409, "해당 노래의 게시글이 이미 존재합니다."),

    // 500
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, 500, "서버 오류가 발생했습니다.");

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
