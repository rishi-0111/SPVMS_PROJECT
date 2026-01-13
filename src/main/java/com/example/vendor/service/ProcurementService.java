package com.example.vendor.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import com.example.vendor.repository.ProcurementOrderRepository;
import com.example.vendor.repository.VendorRepository;
import com.example.vendor.entity.ProcurementOrder;
import com.example.vendor.entity.OrderItem;
import com.example.vendor.entity.Vendor;
import com.example.vendor.dto.ProcurementOrderDTO;
import com.example.vendor.mapper.ProcurementMapper;
import com.example.vendor.exception.ResourceNotFoundException;
import com.example.vendor.exception.InvalidOperationException;

@Service
public class ProcurementService {

    private final ProcurementOrderRepository orderRepo;
    private final VendorRepository vendorRepo;
    private final EmailService emailService;

    public ProcurementService(ProcurementOrderRepository orderRepo, VendorRepository vendorRepo, EmailService emailService) {
        this.orderRepo = orderRepo;
        this.vendorRepo = vendorRepo;
        this.emailService = emailService;
    }

    @Transactional
    public ProcurementOrderDTO createOrder(ProcurementOrderDTO dto) {
        ProcurementOrder order = new ProcurementOrder();
        order.setOrderNumber("PO-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        
        Vendor vendor = vendorRepo.findById(dto.getVendorId())
                .orElseThrow(() -> new ResourceNotFoundException("Vendor not found with id: " + dto.getVendorId()));
        order.setVendor(vendor);
        
        order.setStatus(ProcurementOrder.OrderStatus.DRAFT);
        order.setRequestedBy(dto.getRequestedBy());
        order.setCreatedAt(LocalDateTime.now());
        order.setNotes(dto.getNotes());
        
        // Add items
        List<OrderItem> items = dto.getItems().stream()
                .map(itemDTO -> {
                    OrderItem item = ProcurementMapper.toItemEntity(itemDTO);
                    item.setOrder(order);
                    return item;
                })
                .collect(Collectors.toList());
        order.setItems(items);
        
        // Calculate total
        double total = items.stream()
                .mapToDouble(OrderItem::getLineTotal)
                .sum();
        order.setTotalAmount(total);
        
        ProcurementOrder saved = orderRepo.save(order);
        return ProcurementMapper.toDTO(saved);
    }

    @Transactional
    public ProcurementOrderDTO submitForApproval(Long orderId) {
        ProcurementOrder order = orderRepo.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderId));
        
        if (order.getStatus() != ProcurementOrder.OrderStatus.DRAFT) {
            throw new InvalidOperationException("Only DRAFT orders can be submitted for approval");
        }
        
        order.setStatus(ProcurementOrder.OrderStatus.PENDING_APPROVAL);
        ProcurementOrder saved = orderRepo.save(order);
        
        // Send email notification to approvers
        emailService.sendOrderSubmittedEmail(saved);
        
        return ProcurementMapper.toDTO(saved);
    }

    @Transactional
    public ProcurementOrderDTO approveOrder(Long orderId, String approver) {
        ProcurementOrder order = orderRepo.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderId));
        
        if (order.getStatus() != ProcurementOrder.OrderStatus.PENDING_APPROVAL) {
            throw new InvalidOperationException("Only PENDING_APPROVAL orders can be approved");
        }
        
        order.setStatus(ProcurementOrder.OrderStatus.APPROVED);
        order.setApprovedBy(approver);
        order.setApprovedAt(LocalDateTime.now());
        ProcurementOrder saved = orderRepo.save(order);
        
        // Send email notification to requester
        emailService.sendOrderApprovedEmail(saved);
        
        return ProcurementMapper.toDTO(saved);
    }

    @Transactional
    public ProcurementOrderDTO startProgress(Long orderId) {
        ProcurementOrder order = orderRepo.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderId));
        
        if (order.getStatus() != ProcurementOrder.OrderStatus.APPROVED) {
            throw new InvalidOperationException("Only APPROVED orders can be started");
        }
        
        order.setStatus(ProcurementOrder.OrderStatus.IN_PROGRESS);
        return ProcurementMapper.toDTO(orderRepo.save(order));
    }

    @Transactional
    public ProcurementOrderDTO markDelivered(Long orderId) {
        ProcurementOrder order = orderRepo.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderId));
        
        if (order.getStatus() != ProcurementOrder.OrderStatus.IN_PROGRESS) {
            throw new InvalidOperationException("Only IN_PROGRESS orders can be marked as delivered");
        }
        
        order.setStatus(ProcurementOrder.OrderStatus.DELIVERED);
        order.setDeliveredAt(LocalDateTime.now());
        return ProcurementMapper.toDTO(orderRepo.save(order));
    }

    @Transactional
    public ProcurementOrderDTO cancelOrder(Long orderId) {
        ProcurementOrder order = orderRepo.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderId));
        
        if (order.getStatus() == ProcurementOrder.OrderStatus.DELIVERED) {
            throw new InvalidOperationException("Delivered orders cannot be cancelled");
        }
        
        order.setStatus(ProcurementOrder.OrderStatus.CANCELLED);
        return ProcurementMapper.toDTO(orderRepo.save(order));
    }

    public List<ProcurementOrderDTO> getAllOrders() {
        return orderRepo.findAllOrderByCreatedAtDesc().stream()
                .map(ProcurementMapper::toDTO)
                .collect(Collectors.toList());
    }

    public ProcurementOrderDTO getOrderById(Long id) {
        ProcurementOrder order = orderRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));
        return ProcurementMapper.toDTO(order);
    }

    public List<ProcurementOrderDTO> getOrdersByStatus(String status) {
        ProcurementOrder.OrderStatus orderStatus = ProcurementOrder.OrderStatus.valueOf(status.toUpperCase());
        return orderRepo.findByStatus(orderStatus).stream()
                .map(ProcurementMapper::toDTO)
                .collect(Collectors.toList());
    }

    public List<ProcurementOrderDTO> getOrdersByVendor(Long vendorId) {
        return orderRepo.findByVendorId(vendorId).stream()
                .map(ProcurementMapper::toDTO)
                .collect(Collectors.toList());
    }
}
