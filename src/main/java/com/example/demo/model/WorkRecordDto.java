package com.example.demo.model;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class WorkRecordDto {
    private long id;
    private LocalDateTime rawDateTime;
    private String date;
    private String clockInTime;
    private String clockOutTime;
    private String message;
    private boolean gpsResult;
}
