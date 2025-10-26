package com.fifo.compasstep.apipayload.exceptions.handler;

import com.fifo.compasstep.apipayload.code.BaseErrorCode;
import com.fifo.compasstep.apipayload.exceptions.GeneralException;

public class ReferenceHandler extends GeneralException {
    public ReferenceHandler(BaseErrorCode errorCode) {
        super(errorCode);
    }
}
