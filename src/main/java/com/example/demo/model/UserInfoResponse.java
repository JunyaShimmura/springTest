package com.example.demo.model;

import lombok.Data;

@Data
public class UserInfoResponse {
    private String username;
    private Long todayRecordId;
    private String workPlace;
}
