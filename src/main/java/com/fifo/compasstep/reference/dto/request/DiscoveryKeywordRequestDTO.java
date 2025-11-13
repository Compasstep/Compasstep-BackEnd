package com.fifo.compasstep.reference.dto.request;

import jakarta.validation.constraints.NotBlank;

public record DiscoveryKeywordRequestDTO(
        @NotBlank String query
) {}
