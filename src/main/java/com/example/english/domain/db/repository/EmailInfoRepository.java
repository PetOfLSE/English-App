package com.example.english.domain.db.repository;

import com.example.english.domain.db.entity.EmailInfo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmailInfoRepository extends JpaRepository<EmailInfo, String> {

    boolean existsByEmail(String email);
    EmailInfo findByEmailOrderByIssuedAtDesc(String email);
}
