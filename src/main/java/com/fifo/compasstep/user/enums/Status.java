package com.fifo.compasstep.user.enums;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum Status {

    NORMAL("일반 사용자"),
    SUSPENDED("정지된 사용자"),
    BLOCKED("차단된 사용자"),
    DELETED("삭제된 사용자");

    private final String status;
}
