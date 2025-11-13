package com.fifo.compasstep.metrics.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
public class ApiCountDTO {
    // getters and setters
    @Setter
    private String apiPath;
    private Double count;

    public ApiCountDTO(String apiPath, Double count) {
        this.apiPath = apiPath;
        this.count = count;
    }

    public void setCount(int count) {
        this.count = (double) count;
    }
}
