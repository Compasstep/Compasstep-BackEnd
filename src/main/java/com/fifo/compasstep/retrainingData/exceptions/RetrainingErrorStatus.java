// retrainingData/exceptions/RetrainingErrorStatus.java
package com.fifo.compasstep.retrainingData.exceptions;

import com.fifo.compasstep.apipayload.code.BaseErrorCode;
import com.fifo.compasstep.apipayload.code.ErrorReasonDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum RetrainingErrorStatus implements BaseErrorCode {
    NOT_FOUND(HttpStatus.NOT_FOUND, 404, "대상을 찾을 수 없습니다."),
    ALREADY_REVIEWED(HttpStatus.BAD_REQUEST, 400, "이미 정상 데이터입니다."),
    NOT_REVIEWED_YET(HttpStatus.BAD_REQUEST, 400, "아직 비정상 데이터입니다."),
    INVALID_LABELS(HttpStatus.BAD_REQUEST, 400, "최종 레이블이 비어있거나 형식이 올바르지 않습니다."),
    INVALID_REQUEST(HttpStatus.BAD_REQUEST, 400, "요청 파라미터가 올바르지 않습니다."),
    JSON_PARSE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, 500, "JSON 처리 중 오류가 발생했습니다."),
    DB_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, 500, "데이터 처리 중 오류가 발생했습니다.");

    private final HttpStatus status;
    private final int code;
    private final String message;

    @Override public ErrorReasonDTO getReason() {
        return ErrorReasonDTO.builder().message(message).code(code).build();
    }
    @Override public ErrorReasonDTO getReasonHttpStatus() {
        return ErrorReasonDTO.builder().message(message).code(code).httpStatus(status).build();
    }
}
