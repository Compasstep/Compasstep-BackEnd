package com.fifo.compasstep.reference.dto.response;

import lombok.Builder;

@Builder
public record TrackVideoDto(
        String videoId,
        String title,
        String channelName,
        String thumbnailUrl,
        String youtubeUrl
) {}
