//package com.example.demo.controller;
//
//import com.example.demo.model.WorkRecord;
//import com.example.demo.repository.WorkRecordRepository;
//import com.example.demo.service.WorkRecordService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.*;
//import java.util.List;
//
//@RestController
//@RequestMapping("/api/work-records")
//public class WorkRecordController {
//
//    private final WorkRecordRepository repository;
//    @Autowired
//    private WorkRecordService workRecordService;
//    public WorkRecordController(WorkRecordRepository repository) {
//        this.repository = repository;
//    }
//    //DBのため追加
//    @GetMapping
//    public List<WorkRecord> getAllWorkRecords(){
//        return workRecordService.getAllWorkRecords();
//    }
//    @PostMapping
//    public WorkRecord createWorkRecord(@RequestBody WorkRecord workRecord){
//        return workRecordService.saveWorkRecord(workRecord);
//    }
//    @GetMapping("/{id}")
//    public WorkRecord getWorkRecordById(@PathVariable Long id) {
//        return workRecordService.getWorkRecordById(id).orElse(null);
//    }
//    @DeleteMapping("/{id}")
//    public void deleteWorkRecord(@PathVariable Long id){
//        workRecordService.deleteWorkRecord(id);
//    }
//}
