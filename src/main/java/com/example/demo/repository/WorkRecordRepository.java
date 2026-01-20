package com.example.demo.repository;

import com.example.demo.model.WorkRecord;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WorkRecordRepository extends JpaRepository<WorkRecord,Long> {


    WorkRecord findTopByUsernameOrderByClockInTimeDesc(String username);

    List<WorkRecord> findByUsername(String username, Sort sort);
}
