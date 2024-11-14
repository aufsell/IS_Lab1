package com.aufsell.Lab1.service;

import com.aufsell.Lab1.model.AuditLog;
import com.aufsell.Lab1.repository.AuditLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class AuditLogService {
    @Autowired
    private AuditLogRepository auditLogRepository;

    public void logAction(String actionType, Long objectId, String ObjectName,
                          Long userId, String userName, String details) {
        AuditLog auditLog = new AuditLog();
        auditLog.setActionType(actionType);
        auditLog.setObjectId(objectId);
        auditLog.setObjectName(ObjectName);
        auditLog.setUserId(userId);
        auditLog.setUserName(userName);
        auditLog.setDetails(details);
        auditLogRepository.save(auditLog);
    }

    public Page<AuditLog> getAuditLogs(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Order.desc("createdAt")));
        return auditLogRepository.findAll(pageable);
    }
}
