package com.fifo.compasstep.admin.exceptions;

import com.fifo.compasstep.apipayload.code.BaseErrorCode;
import com.fifo.compasstep.apipayload.code.ErrorReasonDTO;
import com.fifo.compasstep.apipayload.code.ErrorStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum AdminErrorStatus implements BaseErrorCode {

    ACCESS_TOKEN_INVALID(HttpStatus.UNAUTHORIZED, 401, "액세스 토큰이 유효하지 않습니다."),
    CSRF_TOKEN_INVALID(HttpStatus.FORBIDDEN, 401, "CSRF 토큰이 유효하지 않거나 누락되었습니다."),
    JWT_ADMIN_INVALID_TOKEN(HttpStatus.NOT_FOUND, 401, "jwt토큰이 유효하지 않습니다."),
    ANOTHER_ERROR(HttpStatus.NOT_FOUND, 500, "다른 오류."),
    ADMIN_NOT_FOUND(HttpStatus.NOT_FOUND, 403, "관리자가 없습니다."),
    ADMIN_LOGIN_FAILED(HttpStatus.UNAUTHORIZED, 401, "아이디 혹은 비밀번호가 틀렸습니다."),
    ACCESS_DENIED(HttpStatus.UNAUTHORIZED, 403, "권한이 없어 요청이 거부되었습니다"),
    REDIS_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, 500, "Redis 오류가 발생했습니다."),

    REFRESH_TOKEN_NOT_FOUND(HttpStatus.NOT_FOUND, 401, "리프레시 토큰을 찾을 수 없습니다."),
    REFRESH_TOKEN_INVALID(HttpStatus.UNAUTHORIZED, 401, "리프레시 토큰이 유효하지 않습니다."),
    REFRESH_TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, 401, "리프레시 토큰이 만료되었습니다."),

    CANNOT_DELETE_SELF(HttpStatus.FORBIDDEN, 401, "자기 자신을 탈퇴할 수 없습니다."),
    PASSWORD_NOT_MATCH(HttpStatus.FORBIDDEN, 401, "두 비밀번호가 일치하지 않습니다."),
    EMAIL_ALREADY_EXISTS(HttpStatus.FORBIDDEN, 403, "이메일이 중복되었습니다."),
    NOT_SUPPORTED_USERTYPE(HttpStatus.NOT_FOUND, 401, "지원하지 않는 유저타입입니다.")

            ;

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
                .httpStatus(status).build();
    }
}
