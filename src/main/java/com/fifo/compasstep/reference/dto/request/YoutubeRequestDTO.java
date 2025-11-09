package com.fifo.compasstep.reference.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class YoutubeRequestDTO {
    private Long songId;
    private String artistName;
}
