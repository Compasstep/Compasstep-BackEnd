package com.fifo.compasstep.apipayload.exceptions.handler;

import com.fifo.compasstep.apipayload.code.BaseErrorCode;
import com.fifo.compasstep.apipayload.exceptions.GeneralException;

public class ReputationAnalysisHandler extends GeneralException {
    public ReputationAnalysisHandler(BaseErrorCode errorCode) {
        super(errorCode);
    }
}