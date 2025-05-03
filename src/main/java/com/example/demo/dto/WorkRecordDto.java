package com.example.demo.dto;

import com.example.demo.model.WorkRecord;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class WorkRecordDto {
    //元のデータ
    private LocalDateTime lowDateTime;
    //表示用にフォーマットする値
    private String date;
    private String clockInTime;
    private String clockOutTime;

    public WorkRecordDto(WorkRecord entity){
        this.lowDateTime = entity.getClockInTime();
        this.date = ldtFormatDate( entity.getClockInTime() );
        this.clockInTime = ldtFormat(entity.getClockInTime());
        this.clockOutTime = ldtFormat(entity.getClockOutTime());
    }

    public LocalDateTime getLowDateTime() {
        return lowDateTime;
    }

    public void setLowDateTime(LocalDateTime lowDateTime) {
        this.lowDateTime = lowDateTime;
    }
    public String getClockInTime() {
        return clockInTime;
    }

    public void setClockInTime(String clockInTime) {
        this.clockInTime = clockInTime;
    }

    public String getClockOutTime() {
        return clockOutTime;
    }

    public void setClockOutTime(String clockOutTime) {
        this.clockOutTime = clockOutTime;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }




    private String ldtFormat (LocalDateTime ldt){
        if (ldt == null){
            return "";
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        return ldt.format(formatter);
    }
    private String ldtFormatDate(LocalDateTime ldt){
        if (ldt == null){
            return "";
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd");
        return ldt.format(formatter);
    }

}
