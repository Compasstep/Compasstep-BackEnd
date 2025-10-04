package com.fifo.compasstep.apipayload.code;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum SuccessStatus implements BaseCode{

    _OK(HttpStatus.OK, 200, "성공입니다.");
    //세 인수는 바뀌면 안되니 final
    private final HttpStatus status;
    private final int code;
    private final String message;

    @Override
    public ReasonDTO getReason(){
        return ReasonDTO.builder()
                .message(message)
                .code(code)
                .build();
    }
    @Override
    public ReasonDTO getReasonHttpStatus(){
        return ReasonDTO.builder()
                .message(message)
                .code(code)
                .build();
    }
}
