package com.fifo.compasstep.apipayload.exceptions.handler;

import com.fifo.compasstep.apipayload.code.BaseErrorCode;
import com.fifo.compasstep.apipayload.exceptions.GeneralException;

public class LyricsAnalysisHandler extends GeneralException {
    public LyricsAnalysisHandler(BaseErrorCode errorCode) {
        super(errorCode);
    }
}