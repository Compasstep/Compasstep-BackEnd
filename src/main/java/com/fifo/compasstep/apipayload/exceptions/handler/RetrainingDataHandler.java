package com.fifo.compasstep.apipayload.exceptions.handler;

import com.fifo.compasstep.apipayload.code.BaseErrorCode;
import com.fifo.compasstep.apipayload.exceptions.GeneralException;

public class RetrainingDataHandler extends GeneralException {
    public RetrainingDataHandler(BaseErrorCode errorCode) {
        super(errorCode);
    }
}
