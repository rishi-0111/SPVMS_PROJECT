package com.example.vendor.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import jakarta.validation.Valid;
import java.util.List;
import com.example.vendor.service.ProcurementService;
import com.example.vendor.dto.ProcurementOrderDTO;

@RestController
@RequestMapping("/api/procurement")
public class ProcurementController {

    private final ProcurementService service;

    public ProcurementController(ProcurementService service) {
        this.service = service;
    }

    @PostMapping("/orders")
    public ResponseEntity<ProcurementOrderDTO> createOrder(@Valid @RequestBody ProcurementOrderDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.createOrder(dto));
    }

    @GetMapping("/orders")
    public ResponseEntity<List<ProcurementOrderDTO>> getAllOrders() {
        return ResponseEntity.ok(service.getAllOrders());
    }

    @GetMapping("/orders/{id}")
    public ResponseEntity<ProcurementOrderDTO> getOrderById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getOrderById(id));
    }

    @GetMapping("/orders/by-status/{status}")
    public ResponseEntity<List<ProcurementOrderDTO>> getOrdersByStatus(@PathVariable String status) {
        return ResponseEntity.ok(service.getOrdersByStatus(status));
    }

    @GetMapping("/orders/by-vendor/{vendorId}")
    public ResponseEntity<List<ProcurementOrderDTO>> getOrdersByVendor(@PathVariable Long vendorId) {
        return ResponseEntity.ok(service.getOrdersByVendor(vendorId));
    }

    @PostMapping("/orders/{id}/submit")
    public ResponseEntity<ProcurementOrderDTO> submitForApproval(@PathVariable Long id) {
        return ResponseEntity.ok(service.submitForApproval(id));
    }

    @PostMapping("/orders/{id}/approve")
    public ResponseEntity<ProcurementOrderDTO> approveOrder(
            @PathVariable Long id,
            @RequestParam String approver) {
        return ResponseEntity.ok(service.approveOrder(id, approver));
    }

    @PostMapping("/orders/{id}/start")
    public ResponseEntity<ProcurementOrderDTO> startProgress(@PathVariable Long id) {
        return ResponseEntity.ok(service.startProgress(id));
    }

    @PostMapping("/orders/{id}/deliver")
    public ResponseEntity<ProcurementOrderDTO> markDelivered(@PathVariable Long id) {
        return ResponseEntity.ok(service.markDelivered(id));
    }

    @PostMapping("/orders/{id}/cancel")
    public ResponseEntity<ProcurementOrderDTO> cancelOrder(@PathVariable Long id) {
        return ResponseEntity.ok(service.cancelOrder(id));
    }
}
