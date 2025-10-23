package com.fifo.compasstep.user.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class GoogleUserInfo {
    private final String email;
    private final String name;
}
