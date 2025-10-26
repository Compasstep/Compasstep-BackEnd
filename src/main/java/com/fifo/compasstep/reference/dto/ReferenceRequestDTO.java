// src/main/java/com/fifo/compasstep/reference/dto/ReferenceRequestDto.java
package com.fifo.compasstep.reference.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record ReferenceRequestDTO(
        @NotBlank String genre,
        String market,
        @Min(1) @Max(50) Integer limit
) {
    public String marketOrDefault() { return (market == null || market.isBlank()) ? "KR" : market; }
    public int limitOrDefault() { return (limit == null) ? 20 : Math.min(Math.max(limit, 1), 50); }
}
