package com.fifo.compasstep.metrics.exceptions;

import com.fifo.compasstep.apipayload.code.BaseErrorCode;
import com.fifo.compasstep.apipayload.code.ErrorReasonDTO;
import org.springframework.http.HttpStatus;

public enum MetricsErrorStatus implements BaseErrorCode {

    INVALID_REQUEST(HttpStatus.BAD_REQUEST, 400, "잘못된 요청입니다."),
    EXTERNAL_API_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, 500, "외부 API 호출 중 오류가 발생했습니다."),
    TIMEOUT_ERROR(HttpStatus.GATEWAY_TIMEOUT, 504, "요청이 시간 초과되었습니다.");

    private final HttpStatus status;
    private final int code;
    private final String message;

    MetricsErrorStatus(HttpStatus status, int code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }

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
