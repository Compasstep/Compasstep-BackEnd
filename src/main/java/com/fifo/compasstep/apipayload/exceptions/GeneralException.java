package com.fifo.compasstep.apipayload.exceptions;

import com.fifo.compasstep.apipayload.code.BaseErrorCode;
import com.fifo.compasstep.apipayload.code.ErrorReasonDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class GeneralException extends RuntimeException {
    private BaseErrorCode errorCode;
    public ErrorReasonDTO getErrorReason() {return this.errorCode.getReason(); }
    public ErrorReasonDTO getErrorReasonHttpStatus() {return this.errorCode.getReasonHttpStatus(); }

    @Override
    public String getMessage() {return this.errorCode.getReason().getMessage(); }
}
