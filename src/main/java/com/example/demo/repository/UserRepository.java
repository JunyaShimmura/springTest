package com.example.demo.repository;

import com.example.demo.model.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends  JpaRepository<UserEntity,Long>{
       Optional<UserEntity> findByUsername(String username);

       @Query("SELECT u.username FROM UserEntity u")
       List<String> findAllUserName();

}



