// src/main/java/com/fifo/compasstep/chat/exceptions/DiscoveryErrorStatus.java
package com.fifo.compasstep.chat.exceptions;

import com.fifo.compasstep.apipayload.code.BaseErrorCode;
import com.fifo.compasstep.apipayload.code.ErrorReasonDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum DiscoveryErrorStatus implements BaseErrorCode {
    INVALID_REQUEST(HttpStatus.BAD_REQUEST, 400, "잘못된 요청입니다."),
    USER_FORBIDDEN(HttpStatus.FORBIDDEN, 403, "이용이 제한된 사용자입니다."),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, 404, "사용자를 찾을 수 없습니다."),
    FASTAPI_UNPROCESSABLE(HttpStatus.UNPROCESSABLE_ENTITY, 422, "가드레일에 의해 차단되었습니다."),
    FASTAPI_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, 500, "FastAPI 호출 중 오류가 발생했습니다.");

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
