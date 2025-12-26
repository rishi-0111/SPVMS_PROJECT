package com.example.vendor.dto;

public class VendorAnalyticsDTO {
    private Long totalVendors;
    private Double averagePerformanceScore;
    private Double averageDeliveryRate;
    private Double averageQualityRating;
    private Double averagePriceScore;
    private Double maxPerformanceScore;
    private Double minPerformanceScore;
    private Long topPerformersCount;
    private Long lowPerformersCount;

    // Getters and Setters
    public Long getTotalVendors() { return totalVendors; }
    public void setTotalVendors(Long totalVendors) { this.totalVendors = totalVendors; }

    public Double getAveragePerformanceScore() { return averagePerformanceScore; }
    public void setAveragePerformanceScore(Double averagePerformanceScore) { 
        this.averagePerformanceScore = averagePerformanceScore; 
    }

    public Double getAverageDeliveryRate() { return averageDeliveryRate; }
    public void setAverageDeliveryRate(Double averageDeliveryRate) { 
        this.averageDeliveryRate = averageDeliveryRate; 
    }

    public Double getAverageQualityRating() { return averageQualityRating; }
    public void setAverageQualityRating(Double averageQualityRating) { 
        this.averageQualityRating = averageQualityRating; 
    }

    public Double getAveragePriceScore() { return averagePriceScore; }
    public void setAveragePriceScore(Double averagePriceScore) { 
        this.averagePriceScore = averagePriceScore; 
    }

    public Double getMaxPerformanceScore() { return maxPerformanceScore; }
    public void setMaxPerformanceScore(Double maxPerformanceScore) { 
        this.maxPerformanceScore = maxPerformanceScore; 
    }

    public Double getMinPerformanceScore() { return minPerformanceScore; }
    public void setMinPerformanceScore(Double minPerformanceScore) { 
        this.minPerformanceScore = minPerformanceScore; 
    }

    public Long getTopPerformersCount() { return topPerformersCount; }
    public void setTopPerformersCount(Long topPerformersCount) { 
        this.topPerformersCount = topPerformersCount; 
    }

    public Long getLowPerformersCount() { return lowPerformersCount; }
    public void setLowPerformersCount(Long lowPerformersCount) { 
        this.lowPerformersCount = lowPerformersCount; 
    }
}
