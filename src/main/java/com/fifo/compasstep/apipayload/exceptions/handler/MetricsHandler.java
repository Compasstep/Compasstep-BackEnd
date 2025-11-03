package com.fifo.compasstep.apipayload.exceptions.handler;

import com.fifo.compasstep.apipayload.code.BaseErrorCode;
import com.fifo.compasstep.apipayload.exceptions.GeneralException;

public class MetricsHandler extends GeneralException {
    public MetricsHandler(BaseErrorCode errorCode) {
        super(errorCode);
    }
}
