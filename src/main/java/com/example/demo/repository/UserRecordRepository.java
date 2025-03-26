package com.example.demo.repository;

import com.example.demo.model.UserRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRecordRepository extends  JpaRepository<UserRecord,Long>{
       UserRecord findByUsername(String username);

}



