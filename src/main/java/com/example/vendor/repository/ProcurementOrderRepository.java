package com.example.vendor.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;
import com.example.vendor.entity.ProcurementOrder;

public interface ProcurementOrderRepository extends JpaRepository<ProcurementOrder, Long> {
    
    @Query("SELECT o FROM ProcurementOrder o WHERE o.status = :status")
    List<ProcurementOrder> findByStatus(ProcurementOrder.OrderStatus status);
    
    @Query("SELECT o FROM ProcurementOrder o WHERE o.vendor.id = :vendorId")
    List<ProcurementOrder> findByVendorId(Long vendorId);
    
    @Query("SELECT o FROM ProcurementOrder o WHERE o.requestedBy = :user")
    List<ProcurementOrder> findByRequestedBy(String user);
    
    @Query("SELECT o FROM ProcurementOrder o ORDER BY o.createdAt DESC")
    List<ProcurementOrder> findAllOrderByCreatedAtDesc();
}
