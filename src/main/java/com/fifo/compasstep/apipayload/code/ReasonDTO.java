package com.fifo.compasstep.apipayload.code;

import lombok.Builder;
import lombok.Getter;
import org.apache.http.HttpStatus;

@Getter
@Builder
public class ReasonDTO {
    private HttpStatus httpstatus;
    private Integer code;
    private String message;
}
