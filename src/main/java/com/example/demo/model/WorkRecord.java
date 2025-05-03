package com.example.demo.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Table;
import jakarta.persistence.*;
import org.jetbrains.annotations.NotNull;
import org.springframework.lang.NonNull;

import javax.xml.crypto.Data;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;

@Entity
@Table(name = "work_record")
public class WorkRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name="username",nullable = false)
    private String username ;
    @Column(name = "clock_in_time")
    private LocalDateTime clockInTime;
    @Column(name = "clock_in_judge")
    private boolean clockInJudge;
    @Column(name = "clock_out_time")
    private LocalDateTime clockOutTime;
    @Column(name = "clock_out_judge")
    private  boolean clockOutJudge;
    @Column(name = "status")
    private int status; //2:出勤済 3:退勤済

    public WorkRecord() {
    }
    public WorkRecord(String username, LocalDateTime clockInTime, Boolean clockInJudge,
                      LocalDateTime clockOutTime, Boolean clockOutJudge, int status) {
        this.username = username;
        this.clockInTime = clockInTime;
        this.clockInJudge = clockInJudge;
        this.clockOutTime = clockOutTime;
        this.clockOutJudge = clockOutJudge;
        this.status = status;
    }

    public boolean isClockInJudge() {
        return clockInJudge;
    }

    public void setClockInJudge(boolean clockInJudge) {
        this.clockInJudge = clockInJudge;
    }

    public boolean isClockOutJudge() {
        return clockOutJudge;
    }

    public void setClockOutJudge(boolean clockOutJudge) {
        this.clockOutJudge = clockOutJudge;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }


    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public LocalDateTime getClockInTime() {
        return clockInTime;
    }
    public void setClockInTime(LocalDateTime clockInTime) {
        this.clockInTime = clockInTime;
    }
    public LocalDateTime getClockOutTime() {
        return clockOutTime;
    }
    public void setClockOutTime(LocalDateTime clockOutTime) {
        this.clockOutTime = clockOutTime;
    }



}
