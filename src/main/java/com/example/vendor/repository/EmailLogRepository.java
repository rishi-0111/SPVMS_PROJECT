
package com.example.vendor.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;
import com.example.vendor.entity.EmailLog;
import com.example.vendor.entity.EmailLog.EmailStatus;

public interface EmailLogRepository extends JpaRepository<EmailLog, Long> {
    
    List<EmailLog> findByStatus(EmailStatus status);
    
    List<EmailLog> findByRelatedOrderId(Long orderId);
    
    @Query("SELECT e FROM EmailLog e WHERE e.status = :status AND e.retryCount < :maxRetries")
    List<EmailLog> findByStatusAndRetryCountLessThan(EmailStatus status, int maxRetries);
    
    List<EmailLog> findAllByOrderByCreatedAtDesc();
}
