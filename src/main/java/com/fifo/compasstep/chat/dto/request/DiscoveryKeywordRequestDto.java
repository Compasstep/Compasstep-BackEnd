package com.fifo.compasstep.chat.dto.request;

import jakarta.validation.constraints.NotBlank;

public record DiscoveryKeywordRequestDto(
        @NotBlank String query
) {}
