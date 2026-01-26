package com.example.demo.repository;

import com.example.demo.model.WorkRecordEntity;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WorkRecordRepository extends JpaRepository<WorkRecordEntity,Long> {


    WorkRecordEntity findTopByUsernameOrderByClockInTimeDesc(String username);

    List<WorkRecordEntity> findByUsername(String username, Sort sort);
}
