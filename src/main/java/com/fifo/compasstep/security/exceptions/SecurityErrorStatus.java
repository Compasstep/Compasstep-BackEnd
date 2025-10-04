package com.fifo.compasstep.security.exceptions;

import com.fifo.compasstep.apipayload.code.BaseErrorCode;
import com.fifo.compasstep.apipayload.code.ErrorReasonDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum SecurityErrorStatus implements BaseErrorCode {
    INVALID_TOKEN(HttpStatus.NOT_FOUND, 401, "토큰이 유효하지 않습니다."),
    INVALID_CSRF_TOKEN(HttpStatus.NOT_FOUND, 401, "csrf토큰이 유효하지 않습니다."),
    PREAUTHORIZE_FAILED(HttpStatus.FORBIDDEN, 403, "권한이 부족합니다.")
    ;

    private final HttpStatus httpStatus;
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
