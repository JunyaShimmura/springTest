package com.example.demo.service;
import com.example.demo.model.WorkRecord;
import com.example.demo.repository.WorkRecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
@Service
public class WorkRecordService {
    @Autowired
    private WorkRecordRepository workRecordRepository;

    public  List<WorkRecord> getAllWorkRecords(){
        return workRecordRepository.findAll();
    }

    public Optional<WorkRecord> getWorkRecordById(long id){
        return workRecordRepository.findById(id);
    }
    public WorkRecord saveWorkRecord(WorkRecord workRecord){
        return workRecordRepository.save(workRecord);
    }
    public void deleteWorkRecord(long id){
        workRecordRepository.deleteById(id);
    }
    public List<WorkRecord> getUserRecordsByUsername(String username){
        return workRecordRepository.findByUsername(username, Sort.by(Sort.Order.asc("id")) );
    }

}
