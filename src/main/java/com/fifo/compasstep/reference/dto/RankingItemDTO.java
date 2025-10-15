// src/main/java/com/fifo/compasstep/reference/dto/RankingItemDto.java
package com.fifo.compasstep.reference.dto;

public record RankingItemDTO(
        int rank,
        String songTitle,
        String artistName,
        String albumImageUrl
) {}
