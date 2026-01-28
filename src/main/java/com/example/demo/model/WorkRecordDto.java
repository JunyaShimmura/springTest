package com.example.demo.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class WorkRecordDto {
    private long id;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private LocalDateTime clockInTime;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private LocalDateTime clockOutTime;
    private String message;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private boolean gpsResult;
}
