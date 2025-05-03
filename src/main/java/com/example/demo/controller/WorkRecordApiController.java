package com.example.demo.controller;

import com.example.demo.dto.WorkRecordDto;
import com.example.demo.model.WorkRecord;
import com.example.demo.service.WorkRecordService;
import jakarta.servlet.http.HttpSession;
import org.antlr.v4.runtime.tree.pattern.ParseTreePattern;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
// json用
public class WorkRecordApiController {
    @Autowired
    private  final WorkRecordService workRecordService;
    public WorkRecordApiController(WorkRecordService workRecordService){
        this.workRecordService = workRecordService;
    }

    @GetMapping("/work-records")
    public List<WorkRecordDto> getAllWorkRecords(HttpSession session) {
        String userName = (String) session.getAttribute("showUserName");
        return workRecordService.getUserRecordsDto(userName);
    }
//    public List<WorkRecord> getAllWorkRecords(HttpSession session){
//        String userName = (String) session.getAttribute("showUserName");
//        return workRecordService.getUserRecordsByUsernameSort(userName);
//    }
}
