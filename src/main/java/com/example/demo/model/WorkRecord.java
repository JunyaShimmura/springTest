package com.example.demo.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Table;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "work_records")
public class WorkRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;
    private LocalDateTime clockInTime;
    private LocalDateTime clockOutTime;

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
