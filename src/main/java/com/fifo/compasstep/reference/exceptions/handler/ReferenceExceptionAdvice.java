package com.fifo.compasstep.reference.exceptions.handler;

import com.fifo.compasstep.apipayload.ApiResponse;
import com.fifo.compasstep.apipayload.code.ErrorReasonDTO;
import com.fifo.compasstep.apipayload.exceptions.GeneralException;
import com.fifo.compasstep.reference.exceptions.ReferenceErrorStatus;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE) // 가장 먼저 처리
@RestControllerAdvice(basePackages = "com.fifo.compasstep.reference")
public class ReferenceExceptionAdvice {

    /** 비즈니스 예외 → 지정한 코드/메시지 */
    @ExceptionHandler(GeneralException.class)
    public ResponseEntity<ApiResponse<Object>> handleGeneral(GeneralException e) {
        ErrorReasonDTO reason = e.getErrorReasonHttpStatus();
        return ResponseEntity.status(reason.getHttpStatus())
                .body(ApiResponse.onFailure(reason.getCode(), reason.getMessage(), null));
    }

    /** 필수 파라미터 누락(genre/title/artist) */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ApiResponse<Object>> handleMissingParam(MissingServletRequestParameterException e) {
        String name = e.getParameterName();
        if ("genre".equals(name)) {
            var r = ReferenceErrorStatus.GENRE_PARAM_MISSING.getReasonHttpStatus();
            return ResponseEntity.status(r.getHttpStatus())
                    .body(ApiResponse.onFailure(r.getCode(), r.getMessage(), null));
        }
        if ("title".equals(name)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.onFailure(400, "곡 제목을 지정해주세요.", null));
        }
        if ("artist".equals(name)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.onFailure(400, "아티스트를 지정해주세요.", null));
        }
        var r = ReferenceErrorStatus.INVALID_REQUEST.getReasonHttpStatus();
        return ResponseEntity.status(r.getHttpStatus())
                .body(ApiResponse.onFailure(r.getCode(), r.getMessage(), null));
    }

    /** 검증 실패(@NotBlank/@Min/@Max 등) */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiResponse<Object>> handleConstraint(ConstraintViolationException e) {
        var r = ReferenceErrorStatus.INVALID_REQUEST.getReasonHttpStatus();
        return ResponseEntity.status(r.getHttpStatus())
                .body(ApiResponse.onFailure(r.getCode(), r.getMessage(), null));
    }

    /** 타입 변환 실패(limit=abc 등) */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiResponse<Object>> handleTypeMismatch(MethodArgumentTypeMismatchException e) {
        var r = ReferenceErrorStatus.INVALID_REQUEST.getReasonHttpStatus();
        return ResponseEntity.status(r.getHttpStatus())
                .body(ApiResponse.onFailure(r.getCode(), r.getMessage(), null));
    }

    /** 그 외 예외 → 500 */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Object>> handleUnknown(Exception e) {
        log.error("Unhandled exception in reference module", e);
        var r = ReferenceErrorStatus.EXTERNAL_API_ERROR.getReasonHttpStatus();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.onFailure(r.getCode(), r.getMessage(), null));
    }
}
