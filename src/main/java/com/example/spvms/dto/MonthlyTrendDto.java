package com.example.spvms.dto;

import java.math.BigDecimal;

public class MonthlyTrendDto {
    private String month;
    private Long prCount;
    private Long poCount;
    private BigDecimal spending;

    public MonthlyTrendDto(String month, Long prCount, Long poCount, BigDecimal spending) {
        this.month = month;
        this.prCount = prCount;
        this.poCount = poCount;
        this.spending = spending != null ? spending : BigDecimal.ZERO;
    }

    public String getMonth() { return month; }
    public void setMonth(String month) { this.month = month; }

    public Long getPrCount() { return prCount; }
    public void setPrCount(Long prCount) { this.prCount = prCount; }

    public Long getPoCount() { return poCount; }
    public void setPoCount(Long poCount) { this.poCount = poCount; }

    public BigDecimal getSpending() { return spending; }
    public void setSpending(BigDecimal spending) { this.spending = spending; }
}
