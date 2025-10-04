package com.fifo.compasstep.admin.enums;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum Role {

    ROOT("최고 관리자"),
    GENERAL("일반 관리자"),
    DELETED("삭제된 관리자");

    private final String role;
}
