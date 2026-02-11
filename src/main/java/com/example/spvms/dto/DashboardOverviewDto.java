package com.example.spvms.dto;

import java.math.BigDecimal;

public class DashboardOverviewDto {
    private Long totalVendors;
    private Long activeVendors;
    private Long inactiveVendors;
    private Long totalPRs;
    private Long totalPOs;
    private BigDecimal totalProcurementAmount;

    public DashboardOverviewDto(Long totalVendors, Long activeVendors, Long inactiveVendors,
                                Long totalPRs, Long totalPOs, BigDecimal totalProcurementAmount) {
        this.totalVendors = totalVendors;
        this.activeVendors = activeVendors;
        this.inactiveVendors = inactiveVendors;
        this.totalPRs = totalPRs;
        this.totalPOs = totalPOs;
        this.totalProcurementAmount = totalProcurementAmount != null ? totalProcurementAmount : BigDecimal.ZERO;
    }

    public Long getTotalVendors() { return totalVendors; }
    public void setTotalVendors(Long totalVendors) { this.totalVendors = totalVendors; }

    public Long getActiveVendors() { return activeVendors; }
    public void setActiveVendors(Long activeVendors) { this.activeVendors = activeVendors; }

    public Long getInactiveVendors() { return inactiveVendors; }
    public void setInactiveVendors(Long inactiveVendors) { this.inactiveVendors = inactiveVendors; }

    public Long getTotalPRs() { return totalPRs; }
    public void setTotalPRs(Long totalPRs) { this.totalPRs = totalPRs; }

    public Long getTotalPOs() { return totalPOs; }
    public void setTotalPOs(Long totalPOs) { this.totalPOs = totalPOs; }

    public BigDecimal getTotalProcurementAmount() { return totalProcurementAmount; }
    public void setTotalProcurementAmount(BigDecimal totalProcurementAmount) {
        this.totalProcurementAmount = totalProcurementAmount;
    }
}
