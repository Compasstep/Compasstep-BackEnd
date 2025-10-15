// src/main/java/com/fifo/compasstep/reference/dto/RankingItemDto.java
package com.fifo.compasstep.reference.dto;

public record RankingItemDto(
        int rank,
        String songTitle,
        String artistName,
        String albumImageUrl
) {}
