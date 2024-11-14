package com.aufsell.Lab1.repository;

import com.aufsell.Lab1.model.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;

public interface AuditLogRepository  extends JpaRepository<AuditLog, Long> {
    AuditLog findByUserId(Long userId);
}
