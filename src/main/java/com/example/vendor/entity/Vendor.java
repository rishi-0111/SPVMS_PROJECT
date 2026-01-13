
package com.example.vendor.entity;

import jakarta.persistence.*;

@Entity
public class Vendor {
    @Id
    @GeneratedValue
    private Long id;

    private String name;
    private double deliveryRate;
    private double qualityRating;
    private double priceScore;
    private double performanceScore;

    public Long getId() { return id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public double getDeliveryRate() { return deliveryRate; }
    public void setDeliveryRate(double deliveryRate) { this.deliveryRate = deliveryRate; }
    public double getQualityRating() { return qualityRating; }
    public void setQualityRating(double qualityRating) { this.qualityRating = qualityRating; }
    public double getPriceScore() { return priceScore; }
    public void setPriceScore(double priceScore) { this.priceScore = priceScore; }
    public double getPerformanceScore() { return performanceScore; }
    public void setPerformanceScore(double performanceScore) { this.performanceScore = performanceScore; }
}
