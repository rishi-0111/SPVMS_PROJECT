
package com.example.vendor.service;

import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import com.example.vendor.repository.AuditLogRepository;
import com.example.vendor.entity.AuditLog;

@Service
public class AuditService {

    private final AuditLogRepository repo;

    public AuditService(AuditLogRepository repo) {
        this.repo = repo;
    }

    public void log(String user, String action, String path) {
        AuditLog a = new AuditLog();
        a.setUserId(user);
        a.setAction(action);
        a.setRequestPath(path);
        a.setTimestamp(LocalDateTime.now());
        repo.save(a);
    }

    public List<AuditLog> getAll() {
        return repo.findAll();
    }
}
