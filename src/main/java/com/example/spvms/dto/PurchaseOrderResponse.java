package com.example.spvms.dto;

import com.example.spvms.enums.POStatus;
import com.example.spvms.model.PurchaseOrder;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class PurchaseOrderResponse {
    private Long id;
    private String title;
    private BigDecimal subtotal;
    private BigDecimal tax;
    private BigDecimal discount;
    private BigDecimal totalAmount;
    private POStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<PurchaseOrderItemResponse> items;

    public PurchaseOrderResponse(PurchaseOrder po) {
        this.id = po.getId();
        this.title = po.getTitle();
        this.subtotal = po.getSubtotal();
        this.tax = po.getTax();
        this.discount = po.getDiscount();
        this.totalAmount = po.getTotalAmount();
        this.status = po.getStatus();
        this.createdAt = po.getCreatedAt();
        this.updatedAt = po.getUpdatedAt();
        this.items = po.getItems().stream()
                .map(PurchaseOrderItemResponse::new)
                .collect(Collectors.toList());
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public BigDecimal getSubtotal() { return subtotal; }
    public void setSubtotal(BigDecimal subtotal) { this.subtotal = subtotal; }

    public BigDecimal getTax() { return tax; }
    public void setTax(BigDecimal tax) { this.tax = tax; }

    public BigDecimal getDiscount() { return discount; }
    public void setDiscount(BigDecimal discount) { this.discount = discount; }

    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }

    public POStatus getStatus() { return status; }
    public void setStatus(POStatus status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }

    public List<PurchaseOrderItemResponse> getItems() { return items; }
    public void setItems(List<PurchaseOrderItemResponse> items) { this.items = items; }
}