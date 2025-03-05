package com.example.demo.repository;

import com.example.demo.model.WorkRecord;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkRecordRepository extends JpaRepository<WorkRecord,Long> {
    WorkRecord findTopByUsernameAndClockOutTimeIsNullOrderByClockInTimeDesc(String username);

    WorkRecord findTopByUsernameOrderById(String username);

    WorkRecord findTopByUsernameOrderByIdDesc(String username);


    WorkRecord findTopByUsername(String username);

    WorkRecord findTopByUsernameOrderByClockInTimeDesc(String username);
}
