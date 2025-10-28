package com.fifo.compasstep.apipayload.exceptions.handler;

import com.fifo.compasstep.apipayload.code.BaseErrorCode;
import com.fifo.compasstep.apipayload.exceptions.GeneralException;

public class GuestCommentHandler extends GeneralException {
    public GuestCommentHandler(BaseErrorCode errorCode) {
        super(errorCode);
    }
}
