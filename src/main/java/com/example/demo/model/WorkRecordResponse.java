package com.example.demo.model;

import lombok.Data;

@Data
public class WorkRecordResponse {
    long id;
    String message;
    String clockInTime;
    String clockOutTime;
    boolean gpsResult;
}
