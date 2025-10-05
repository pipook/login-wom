package com.wom.login.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.wom.login.models.AuditLog;
import com.wom.login.repositories.AuditLogRepository;

@Service
public class AuditService {
    @Autowired
    private AuditLogRepository repo;

    public void log(Long userId, String event, String details, String ip) {
        AuditLog a = new AuditLog();
        a.setUserId(userId);
        a.setEvent(event);
        a.setDetails(details);
        a.setIp(ip);
        repo.save(a);
    }
}
