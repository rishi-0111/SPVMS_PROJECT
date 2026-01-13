package com.example.vendor.mapper;

import com.example.vendor.dto.ProcurementOrderDTO;
import com.example.vendor.dto.OrderItemDTO;
import com.example.vendor.entity.ProcurementOrder;
import com.example.vendor.entity.OrderItem;
import java.util.stream.Collectors;

public class ProcurementMapper {

    public static ProcurementOrderDTO toDTO(ProcurementOrder order) {
        ProcurementOrderDTO dto = new ProcurementOrderDTO();
        dto.setId(order.getId());
        dto.setOrderNumber(order.getOrderNumber());
        if (order.getVendor() != null) {
            dto.setVendorId(order.getVendor().getId());
            dto.setVendorName(order.getVendor().getName());
        }
        dto.setItems(order.getItems().stream()
                .map(ProcurementMapper::toItemDTO)
                .collect(Collectors.toList()));
        dto.setStatus(order.getStatus() != null ? order.getStatus().name() : null);
        dto.setRequestedBy(order.getRequestedBy());
        dto.setApprovedBy(order.getApprovedBy());
        dto.setCreatedAt(order.getCreatedAt());
        dto.setApprovedAt(order.getApprovedAt());
        dto.setDeliveredAt(order.getDeliveredAt());
        dto.setTotalAmount(order.getTotalAmount());
        dto.setNotes(order.getNotes());
        return dto;
    }

    public static OrderItemDTO toItemDTO(OrderItem item) {
        OrderItemDTO dto = new OrderItemDTO();
        dto.setId(item.getId());
        dto.setItemName(item.getItemName());
        dto.setDescription(item.getDescription());
        dto.setQuantity(item.getQuantity());
        dto.setUnitPrice(item.getUnitPrice());
        dto.setLineTotal(item.getLineTotal());
        return dto;
    }

    public static OrderItem toItemEntity(OrderItemDTO dto) {
        OrderItem item = new OrderItem();
        item.setId(dto.getId());
        item.setItemName(dto.getItemName());
        item.setDescription(dto.getDescription());
        item.setQuantity(dto.getQuantity());
        item.setUnitPrice(dto.getUnitPrice());
        item.setLineTotal(dto.getQuantity() * dto.getUnitPrice());
        return item;
    }
}
