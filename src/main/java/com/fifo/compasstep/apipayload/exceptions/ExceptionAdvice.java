package com.fifo.compasstep.apipayload.exceptions;

import com.fifo.compasstep.apipayload.ApiResponse;
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

    /** 마지막 안전망 → 500 */
    @ExceptionHandler(Exception.class)
    @SuppressWarnings("unused")
    public ResponseEntity<Object> handleUnknown(Exception e, HttpServletRequest request) {
        log.error("Unhandled exception", e);
        var r = ReferenceErrorStatus.EXTERNAL_API_ERROR.getReasonHttpStatus();
        return handleExceptionInternal(e, r, null, request);
    }
}