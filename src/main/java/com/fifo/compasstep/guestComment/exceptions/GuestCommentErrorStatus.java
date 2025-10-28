// src/main/java/com/fifo/compasstep/guestComment/exceptions/GuestCommentErrorStatus.java
package com.fifo.compasstep.guestComment.exceptions;

import com.fifo.compasstep.apipayload.code.BaseErrorCode;
import com.fifo.compasstep.apipayload.code.ErrorReasonDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum GuestCommentErrorStatus implements BaseErrorCode {

    // 400
    COMMENT_REQUIRED(HttpStatus.BAD_REQUEST, 400, "댓글 내용을 입력하세요."),
    INVALID_REQUEST(HttpStatus.BAD_REQUEST, 400, "잘못된 요청입니다."),

    // 404
    POST_NOT_FOUND(HttpStatus.NOT_FOUND, 404, "게시글을 찾을 수 없습니다.");

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
