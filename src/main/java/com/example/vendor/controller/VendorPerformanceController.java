
package com.example.vendor.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import jakarta.validation.Valid;
import java.util.List;
import com.example.vendor.service.VendorPerformanceService;
import com.example.vendor.entity.Vendor;
import com.example.vendor.dto.VendorDTO;

@RestController
@RequestMapping("/api/vendors")
public class VendorPerformanceController {

    private final VendorPerformanceService service;

    public VendorPerformanceController(VendorPerformanceService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<VendorDTO>> getAllVendors() {
        return ResponseEntity.ok(service.getAllVendors());
    }

    @GetMapping("/{id}")
    public ResponseEntity<VendorDTO> getVendorById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getVendorById(id));
    }

    @PostMapping
    public ResponseEntity<VendorDTO> createVendor(@Valid @RequestBody VendorDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.createVendor(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<VendorDTO> updateVendor(
            @PathVariable Long id,
            @Valid @RequestBody VendorDTO dto) {
        return ResponseEntity.ok(service.updateVendor(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteVendor(@PathVariable Long id) {
        service.deleteVendor(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/performance")
    public ResponseEntity<List<Vendor>> top(@RequestParam(defaultValue = "70") double minScore) {
        return ResponseEntity.ok(service.top(minScore));
    }

    @PostMapping("/{id}/recalculate-score")
    public ResponseEntity<VendorDTO> recalculateScore(@PathVariable Long id) {
        return ResponseEntity.ok(service.recalculateVendorScore(id));
    }

    @PostMapping("/recalculate-all-scores")
    public ResponseEntity<String> recalculateAllScores() {
        service.updateScores();
        return ResponseEntity.ok("All vendor scores recalculated successfully");
    }
}
