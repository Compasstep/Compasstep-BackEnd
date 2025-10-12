// src/main/java/com/fifo/compasstep/reference/dto/YoutubeLinkRequestDto.java
package com.fifo.compasstep.reference.dto;

import jakarta.validation.constraints.NotBlank;

public record YoutubeLinkRequestDto(
        @NotBlank String title,
        @NotBlank String artist   // optional
) {}
