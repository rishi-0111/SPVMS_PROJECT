
package com.example.vendor.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;
import com.example.vendor.repository.VendorRepository;
import com.example.vendor.entity.Vendor;
import com.example.vendor.dto.VendorDTO;
import com.example.vendor.mapper.VendorMapper;
import com.example.vendor.exception.ResourceNotFoundException;

@Service
public class VendorPerformanceService {

    private final VendorRepository repo;

    public VendorPerformanceService(VendorRepository repo) {
        this.repo = repo;
    }

    public double calculate(Vendor v) {
        return v.getDeliveryRate() * 0.4 +
               v.getQualityRating() * 20 * 0.4 +
               v.getPriceScore() * 0.2;
    }

    @Transactional
    public void updateScores() {
        for (Vendor v : repo.findAll()) {
            v.setPerformanceScore(calculate(v));
            repo.save(v);
        }
    }

    @Transactional
    public VendorDTO recalculateVendorScore(Long vendorId) {
        Vendor vendor = repo.findById(vendorId)
                .orElseThrow(() -> new ResourceNotFoundException("Vendor not found with id: " + vendorId));
        vendor.setPerformanceScore(calculate(vendor));
        return VendorMapper.toDTO(repo.save(vendor));
    }

    public List<Vendor> top(double min) {
        return repo.top(min);
    }

    public List<VendorDTO> getAllVendors() {
        return repo.findAll().stream()
                .map(VendorMapper::toDTO)
                .collect(Collectors.toList());
    }

    public VendorDTO getVendorById(Long id) {
        Vendor vendor = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vendor not found with id: " + id));
        return VendorMapper.toDTO(vendor);
    }

    @Transactional
    public VendorDTO createVendor(VendorDTO dto) {
        Vendor vendor = VendorMapper.toEntity(dto);
        vendor.setPerformanceScore(calculate(vendor));
        return VendorMapper.toDTO(repo.save(vendor));
    }

    @Transactional
    public VendorDTO updateVendor(Long id, VendorDTO dto) {
        Vendor vendor = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vendor not found with id: " + id));
        
        vendor.setName(dto.getName());
        vendor.setDeliveryRate(dto.getDeliveryRate());
        vendor.setQualityRating(dto.getQualityRating());
        vendor.setPriceScore(dto.getPriceScore());
        vendor.setPerformanceScore(calculate(vendor));
        
        return VendorMapper.toDTO(repo.save(vendor));
    }

    @Transactional
    public void deleteVendor(Long id) {
        if (!repo.existsById(id)) {
            throw new ResourceNotFoundException("Vendor not found with id: " + id);
        }
        repo.deleteById(id);
    }
}
