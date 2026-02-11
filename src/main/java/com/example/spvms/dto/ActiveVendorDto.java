package com.example.spvms.dto;

public class ActiveVendorDto {
    private Long vendorId;
    private String vendorName;
    private Double rating;
    private String category;
    private String location;

    public ActiveVendorDto(Long vendorId, String vendorName, Double rating, String category, String location) {
        this.vendorId = vendorId;
        this.vendorName = vendorName;
        this.rating = rating;
        this.category = category;
        this.location = location;
    }

    public Long getVendorId() { return vendorId; }
    public void setVendorId(Long vendorId) { this.vendorId = vendorId; }

    public String getVendorName() { return vendorName; }
    public void setVendorName(String vendorName) { this.vendorName = vendorName; }

    public Double getRating() { return rating; }
    public void setRating(Double rating) { this.rating = rating; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
}
