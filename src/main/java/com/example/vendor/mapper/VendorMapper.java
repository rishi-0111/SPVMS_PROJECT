package com.example.vendor.mapper;

import com.example.vendor.dto.VendorDTO;
import com.example.vendor.entity.Vendor;

public class VendorMapper {

    public static VendorDTO toDTO(Vendor vendor) {
        VendorDTO dto = new VendorDTO();
        dto.setId(vendor.getId());
        dto.setName(vendor.getName());
        dto.setDeliveryRate(vendor.getDeliveryRate());
        dto.setQualityRating(vendor.getQualityRating());
        dto.setPriceScore(vendor.getPriceScore());
        dto.setPerformanceScore(vendor.getPerformanceScore());
        return dto;
    }

    public static Vendor toEntity(VendorDTO dto) {
        Vendor vendor = new Vendor();
        vendor.setName(dto.getName());
        vendor.setDeliveryRate(dto.getDeliveryRate());
        vendor.setQualityRating(dto.getQualityRating());
        vendor.setPriceScore(dto.getPriceScore());
        return vendor;
    }
}
