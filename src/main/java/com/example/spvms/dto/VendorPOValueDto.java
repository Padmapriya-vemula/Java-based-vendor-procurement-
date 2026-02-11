package com.example.spvms.dto;

import java.math.BigDecimal;

public class VendorPOValueDto {
    private Long vendorId;
    private String vendorName;
    private BigDecimal totalValue;

    public VendorPOValueDto(Long vendorId, String vendorName, BigDecimal totalValue) {
        this.vendorId = vendorId;
        this.vendorName = vendorName;
        this.totalValue = totalValue != null ? totalValue : BigDecimal.ZERO;
    }

    public Long getVendorId() { return vendorId; }
    public void setVendorId(Long vendorId) { this.vendorId = vendorId; }

    public String getVendorName() { return vendorName; }
    public void setVendorName(String vendorName) { this.vendorName = vendorName; }

    public BigDecimal getTotalValue() { return totalValue; }
    public void setTotalValue(BigDecimal totalValue) { this.totalValue = totalValue; }
}
