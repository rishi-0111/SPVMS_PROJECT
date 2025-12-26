package com.example.vendor.dto;

import jakarta.validation.constraints.*;

public class VendorDTO {
    private Long id;

    @NotBlank(message = "Vendor name is required")
    @Size(min = 2, max = 200, message = "Vendor name must be between 2 and 200 characters")
    private String name;

    @NotNull(message = "Delivery rate is required")
    @Min(value = 0, message = "Delivery rate must be between 0 and 100")
    @Max(value = 100, message = "Delivery rate must be between 0 and 100")
    private Double deliveryRate;

    @NotNull(message = "Quality rating is required")
    @DecimalMin(value = "0.0", message = "Quality rating must be between 0 and 5")
    @DecimalMax(value = "5.0", message = "Quality rating must be between 0 and 5")
    private Double qualityRating;

    @NotNull(message = "Price score is required")
    @Min(value = 0, message = "Price score must be between 0 and 100")
    @Max(value = 100, message = "Price score must be between 0 and 100")
    private Double priceScore;

    private Double performanceScore;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Double getDeliveryRate() { return deliveryRate; }
    public void setDeliveryRate(Double deliveryRate) { this.deliveryRate = deliveryRate; }

    public Double getQualityRating() { return qualityRating; }
    public void setQualityRating(Double qualityRating) { this.qualityRating = qualityRating; }

    public Double getPriceScore() { return priceScore; }
    public void setPriceScore(Double priceScore) { this.priceScore = priceScore; }

    public Double getPerformanceScore() { return performanceScore; }
    public void setPerformanceScore(Double performanceScore) { this.performanceScore = performanceScore; }
}
