package com.example.vendor.service;

import org.springframework.stereotype.Service;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import com.example.vendor.repository.VendorRepository;
import com.example.vendor.entity.Vendor;
import com.example.vendor.dto.VendorAnalyticsDTO;

@Service
public class VendorAnalyticsService {

    private final VendorRepository vendorRepo;
    private final EntityManager entityManager;

    public VendorAnalyticsService(VendorRepository vendorRepo, EntityManager entityManager) {
        this.vendorRepo = vendorRepo;
        this.entityManager = entityManager;
    }

    /**
     * Get top N vendors by performance score using JPQL
     */
    public List<Vendor> getTopVendors(int limit) {
        String jpql = "SELECT v FROM Vendor v ORDER BY v.performanceScore DESC";
        return entityManager.createQuery(jpql, Vendor.class)
                .setMaxResults(limit)
                .getResultList();
    }

    /**
     * Get vendors within a performance score range using JPQL
     */
    public List<Vendor> getVendorsByScoreRange(double minScore, double maxScore) {
        String jpql = "SELECT v FROM Vendor v WHERE v.performanceScore BETWEEN :min AND :max ORDER BY v.performanceScore DESC";
        return entityManager.createQuery(jpql, Vendor.class)
                .setParameter("min", minScore)
                .setParameter("max", maxScore)
                .getResultList();
    }

    /**
     * Get vendors with delivery rate above threshold using JPQL
     */
    public List<Vendor> getVendorsByDeliveryRate(double minDeliveryRate) {
        String jpql = "SELECT v FROM Vendor v WHERE v.deliveryRate >= :rate ORDER BY v.deliveryRate DESC";
        return entityManager.createQuery(jpql, Vendor.class)
                .setParameter("rate", minDeliveryRate)
                .getResultList();
    }

    /**
     * Get vendors with quality rating above threshold using JPQL
     */
    public List<Vendor> getVendorsByQualityRating(double minRating) {
        String jpql = "SELECT v FROM Vendor v WHERE v.qualityRating >= :rating ORDER BY v.qualityRating DESC";
        return entityManager.createQuery(jpql, Vendor.class)
                .setParameter("rating", minRating)
                .getResultList();
    }

    /**
     * Get comprehensive analytics using multiple JPQL queries
     */
    public VendorAnalyticsDTO getComprehensiveAnalytics() {
        VendorAnalyticsDTO analytics = new VendorAnalyticsDTO();
        
        // Total vendors
        String countJpql = "SELECT COUNT(v) FROM Vendor v";
        Long totalVendors = entityManager.createQuery(countJpql, Long.class)
                .getSingleResult();
        analytics.setTotalVendors(totalVendors);
        
        if (totalVendors > 0) {
            // Average performance score
            String avgPerfJpql = "SELECT AVG(v.performanceScore) FROM Vendor v";
            Double avgPerf = entityManager.createQuery(avgPerfJpql, Double.class)
                    .getSingleResult();
            analytics.setAveragePerformanceScore(avgPerf != null ? Math.round(avgPerf * 100.0) / 100.0 : 0.0);
            
            // Average delivery rate
            String avgDelJpql = "SELECT AVG(v.deliveryRate) FROM Vendor v";
            Double avgDel = entityManager.createQuery(avgDelJpql, Double.class)
                    .getSingleResult();
            analytics.setAverageDeliveryRate(avgDel != null ? Math.round(avgDel * 100.0) / 100.0 : 0.0);
            
            // Average quality rating
            String avgQualJpql = "SELECT AVG(v.qualityRating) FROM Vendor v";
            Double avgQual = entityManager.createQuery(avgQualJpql, Double.class)
                    .getSingleResult();
            analytics.setAverageQualityRating(avgQual != null ? Math.round(avgQual * 100.0) / 100.0 : 0.0);
            
            // Average price score
            String avgPriceJpql = "SELECT AVG(v.priceScore) FROM Vendor v";
            Double avgPrice = entityManager.createQuery(avgPriceJpql, Double.class)
                    .getSingleResult();
            analytics.setAveragePriceScore(avgPrice != null ? Math.round(avgPrice * 100.0) / 100.0 : 0.0);
            
            // Max performance score
            String maxJpql = "SELECT MAX(v.performanceScore) FROM Vendor v";
            Double max = entityManager.createQuery(maxJpql, Double.class)
                    .getSingleResult();
            analytics.setMaxPerformanceScore(max != null ? Math.round(max * 100.0) / 100.0 : 0.0);
            
            // Min performance score
            String minJpql = "SELECT MIN(v.performanceScore) FROM Vendor v";
            Double min = entityManager.createQuery(minJpql, Double.class)
                    .getSingleResult();
            analytics.setMinPerformanceScore(min != null ? Math.round(min * 100.0) / 100.0 : 0.0);
            
            // Top performers (score >= 80)
            String topJpql = "SELECT COUNT(v) FROM Vendor v WHERE v.performanceScore >= 80";
            Long topCount = entityManager.createQuery(topJpql, Long.class)
                    .getSingleResult();
            analytics.setTopPerformersCount(topCount);
            
            // Low performers (score < 50)
            String lowJpql = "SELECT COUNT(v) FROM Vendor v WHERE v.performanceScore < 50";
            Long lowCount = entityManager.createQuery(lowJpql, Long.class)
                    .getSingleResult();
            analytics.setLowPerformersCount(lowCount);
        }
        
        return analytics;
    }

    /**
     * Get performance distribution (count by score ranges)
     */
    public Map<String, Long> getPerformanceDistribution() {
        Map<String, Long> distribution = new HashMap<>();
        
        String excellent = "SELECT COUNT(v) FROM Vendor v WHERE v.performanceScore >= 90";
        distribution.put("excellent_90_plus", 
                entityManager.createQuery(excellent, Long.class).getSingleResult());
        
        String good = "SELECT COUNT(v) FROM Vendor v WHERE v.performanceScore >= 70 AND v.performanceScore < 90";
        distribution.put("good_70_to_89", 
                entityManager.createQuery(good, Long.class).getSingleResult());
        
        String average = "SELECT COUNT(v) FROM Vendor v WHERE v.performanceScore >= 50 AND v.performanceScore < 70";
        distribution.put("average_50_to_69", 
                entityManager.createQuery(average, Long.class).getSingleResult());
        
        String poor = "SELECT COUNT(v) FROM Vendor v WHERE v.performanceScore < 50";
        distribution.put("poor_below_50", 
                entityManager.createQuery(poor, Long.class).getSingleResult());
        
        return distribution;
    }

    /**
     * Search vendors by name pattern using JPQL
     */
    public List<Vendor> searchVendorsByName(String namePattern) {
        String jpql = "SELECT v FROM Vendor v WHERE LOWER(v.name) LIKE LOWER(:pattern) ORDER BY v.name";
        return entityManager.createQuery(jpql, Vendor.class)
                .setParameter("pattern", "%" + namePattern + "%")
                .getResultList();
    }
}
