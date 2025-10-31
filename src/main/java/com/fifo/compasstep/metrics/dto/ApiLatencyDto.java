package com.fifo.compasstep.metrics.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ApiLatencyDto {
    // getters and setters
    private String apiPath;
    private double latency;

    public ApiLatencyDto(String apiPath, double latency) {
        this.apiPath = apiPath;
        this.latency = latency;
    }

}
