package com.fifo.compasstep.apipayload.exceptions;

import com.fifo.compasstep.apipayload.ApiResponse;
import com.fifo.compasstep.apipayload.code.ErrorReasonDTO;
import com.fifo.compasstep.reference.exceptions.ReferenceErrorStatus;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.TypeMismatchException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
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

    /* ========= 부모 훅 override ========= */

    /** 필수 파라미터 누락(genre/title/artist 등) → 400 */
    @Override
    protected ResponseEntity<Object> handleMissingServletRequestParameter(
            MissingServletRequestParameterException ex,
            @Nullable HttpHeaders headers,
            HttpStatusCode status,
            WebRequest webRequest
    ) {
        ErrorReasonDTO reason;
        switch (ex.getParameterName()) {
            case "genre" -> reason = ReferenceErrorStatus.GENRE_PARAM_MISSING.getReasonHttpStatus();
            case "title" -> reason = ErrorReasonDTO.builder()
                    .httpStatus(HttpStatus.BAD_REQUEST)
                    .code(400)
                    .message("곡 제목을 지정해주세요.")
                    .build();
            case "artist" -> reason = ErrorReasonDTO.builder()
                    .httpStatus(HttpStatus.BAD_REQUEST)
                    .code(400)
                    .message("아티스트를 지정해주세요.")
                    .build();
            default -> reason = ReferenceErrorStatus.INVALID_REQUEST.getReasonHttpStatus();
        }

        ApiResponse<Object> body = ApiResponse.onFailure(reason.getCode(), reason.getMessage(), null);
        return super.handleExceptionInternal(ex, body, headers, reason.getHttpStatus(), webRequest);
    }

    /** 타입 변환 실패(limit=abc 등) → 400 */
    @Override
    protected ResponseEntity<Object> handleTypeMismatch(
            TypeMismatchException ex,
            @Nullable HttpHeaders headers,
            HttpStatusCode status,
            WebRequest webRequest
    ) {
        var r = ReferenceErrorStatus.INVALID_REQUEST.getReasonHttpStatus();
        ApiResponse<Object> body = ApiResponse.onFailure(r.getCode(), r.getMessage(), null);
        return super.handleExceptionInternal(ex, body, headers, r.getHttpStatus(), webRequest);
    }
}