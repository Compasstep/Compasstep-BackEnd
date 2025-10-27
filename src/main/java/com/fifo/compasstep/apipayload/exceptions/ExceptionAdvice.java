package com.fifo.compasstep.apipayload.exceptions;

import com.fifo.compasstep.admin.exceptions.AdminErrorStatus;
import com.fifo.compasstep.apipayload.ApiResponse;
import com.fifo.compasstep.apipayload.code.BaseErrorCode;
import com.fifo.compasstep.apipayload.code.ErrorReasonDTO;
import com.fifo.compasstep.reference.exceptions.ReferenceErrorStatus;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.nio.file.AccessDeniedException;
import java.security.GeneralSecurityException;

@Slf4j
@RestControllerAdvice
public class ExceptionAdvice extends ResponseEntityExceptionHandler {
    // 여기서 오류 사항이 있는 경우 인터셉트 후 미리 정한 양식에 맞춰 반환

    @ExceptionHandler(GeneralSecurityException.class)
    public ResponseEntity<Object> onThrowException(GeneralException generalException, HttpServletRequest request) {
        ErrorReasonDTO reason = generalException.getErrorReasonHttpStatus();
        return handleExceptionInternal(generalException, reason, null, request);

    }

    private ResponseEntity<Object> handleExceptionInternal(Exception e, ErrorReasonDTO reason,
                                                           HttpHeaders headers, HttpServletRequest request) {
        ApiResponse<Object> body = ApiResponse.onFailure(reason.getCode(), reason.getMessage(), null);
        WebRequest webRequest = new ServletWebRequest(request);
        return super.handleExceptionInternal(e, body, headers, reason.getHttpStatus(), webRequest);
    }

    /** 공통 비즈니스 예외 → 지정한 코드/메시지/상태 */
    @ExceptionHandler(GeneralException.class)
    @SuppressWarnings("unused")
    public ResponseEntity<Object> handleGeneral(GeneralException e, HttpServletRequest request) {
        ErrorReasonDTO reason = e.getErrorReasonHttpStatus();
        return handleExceptionInternal(e, reason, null, request);
    }

    /** Bean Validation 위반(@NotBlank/@Min/@Max 등) → 400 */
    @ExceptionHandler(ConstraintViolationException.class)
    @SuppressWarnings("unused")
    public ResponseEntity<Object> handleConstraint(ConstraintViolationException e, HttpServletRequest request) {
        var r = ReferenceErrorStatus.INVALID_REQUEST.getReasonHttpStatus();
        return handleExceptionInternal(e, r, null, request);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Object> handleAccessDenied(AccessDeniedException ex, HttpServletRequest request) {
        // 1. 미리 정의된 에러 코드/상태를 가져옵니다 (예: SecurityErrorStatus.ACCESS_DENIED)
        BaseErrorCode errorCode = AdminErrorStatus.ACCESS_DENIED; // ACCESS_DENIED를 SecurityErrorStatus에 정의해야 함

        // 2. ApiResponse 형식으로 실패 응답 본문을 만듭니다.
        ApiResponse<Object> body = ApiResponse.onFailure(
                errorCode.getReason().getCode(),    // 예: "AUTH003"
                errorCode.getReason().getMessage(), // 예: "접근 권한이 없습니다."
                null                               // 추가 데이터 없음
        );

        // 3. ResponseEntity에 본문과 HTTP 상태 코드(403 Forbidden)를 담아 반환합니다.
        return ResponseEntity.status(errorCode.getReasonHttpStatus().getHttpStatus()) // HttpStatus.FORBIDDEN
                .body(body);
    }

//    /** 마지막 안전망 → 500 */
//    @ExceptionHandler(Exception.class)
//    @SuppressWarnings("unused")
//    public ResponseEntity<Object> handleUnknown(Exception e, HttpServletRequest request) {
//        log.error("Unhandled exception", e);
//        var r = ReferenceErrorStatus.EXTERNAL_API_ERROR.getReasonHttpStatus();
//        return handleExceptionInternal(e, r, null, request);
//    }
}