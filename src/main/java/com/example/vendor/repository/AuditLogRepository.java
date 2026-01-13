
package com.example.vendor.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.vendor.entity.AuditLog;

public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {}
