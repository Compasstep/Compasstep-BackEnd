package com.fifo.compasstep.apipayload.exceptions.handler;

import com.fifo.compasstep.apipayload.code.BaseErrorCode;
import com.fifo.compasstep.apipayload.exceptions.GeneralException;

public class AdminHandler extends GeneralException {
    public AdminHandler(BaseErrorCode errorCode) {
        super(errorCode);
    }
}
