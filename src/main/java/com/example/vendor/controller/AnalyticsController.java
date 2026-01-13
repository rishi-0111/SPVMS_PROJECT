package com.example.vendor.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import java.util.List;
import java.util.Map;
import com.example.vendor.service.VendorAnalyticsService;
import com.example.vendor.entity.Vendor;
import com.example.vendor.dto.VendorAnalyticsDTO;

@RestController
@RequestMapping("/api/analytics")
public class AnalyticsController {

    private final VendorAnalyticsService analyticsService;

    public AnalyticsController(VendorAnalyticsService analyticsService) {
        this.analyticsService = analyticsService;
    }

    @GetMapping("/vendors/top")
    public ResponseEntity<List<Vendor>> getTopVendors(
            @RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(analyticsService.getTopVendors(limit));
    }

    @GetMapping("/vendors/by-score-range")
    public ResponseEntity<List<Vendor>> getVendorsByScoreRange(
            @RequestParam(defaultValue = "0") double minScore,
            @RequestParam(defaultValue = "100") double maxScore) {
        return ResponseEntity.ok(analyticsService.getVendorsByScoreRange(minScore, maxScore));
    }

    @GetMapping("/vendors/by-delivery-rate")
    public ResponseEntity<List<Vendor>> getVendorsByDeliveryRate(
            @RequestParam(defaultValue = "90") double minDeliveryRate) {
        return ResponseEntity.ok(analyticsService.getVendorsByDeliveryRate(minDeliveryRate));
    }

    @GetMapping("/vendors/by-quality-rating")
    public ResponseEntity<List<Vendor>> getVendorsByQualityRating(
            @RequestParam(defaultValue = "4.0") double minRating) {
        return ResponseEntity.ok(analyticsService.getVendorsByQualityRating(minRating));
    }

    @GetMapping("/vendors/search")
    public ResponseEntity<List<Vendor>> searchVendorsByName(
            @RequestParam String name) {
        return ResponseEntity.ok(analyticsService.searchVendorsByName(name));
    }

    @GetMapping("/summary")
    public ResponseEntity<VendorAnalyticsDTO> getComprehensiveAnalytics() {
        return ResponseEntity.ok(analyticsService.getComprehensiveAnalytics());
    }

    @GetMapping("/distribution")
    public ResponseEntity<Map<String, Long>> getPerformanceDistribution() {
        return ResponseEntity.ok(analyticsService.getPerformanceDistribution());
    }
}
