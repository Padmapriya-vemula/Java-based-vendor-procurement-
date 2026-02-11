package com.example.spvms.dto;

import com.example.spvms.enums.ItemStatus;
import com.example.spvms.model.PurchaseOrderItem;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class PurchaseOrderItemResponse {
    private Long id;
    private String itemName;
    private Integer quantity;
    private BigDecimal unitPrice;
    private BigDecimal tax;
    private BigDecimal discount;
    private BigDecimal itemSubtotal;
    private BigDecimal itemTotal;
    private ItemStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long purchaseOrderId;

    public PurchaseOrderItemResponse(PurchaseOrderItem item) {
        this.id = item.getId();
        this.itemName = item.getItemName();
        this.quantity = item.getQuantity();
        this.unitPrice = item.getUnitPrice();
        this.tax = item.getTax();
        this.discount = item.getDiscount();
        this.itemSubtotal = item.getItemSubtotal();
        this.itemTotal = item.getItemTotal();
        this.status = item.getStatus();
        this.createdAt = item.getCreatedAt();
        this.updatedAt = item.getUpdatedAt();
        this.purchaseOrderId = item.getPurchaseOrder() != null ? item.getPurchaseOrder().getId() : null;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getItemName() { return itemName; }
    public void setItemName(String itemName) { this.itemName = itemName; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }

    public BigDecimal getUnitPrice() { return unitPrice; }
    public void setUnitPrice(BigDecimal unitPrice) { this.unitPrice = unitPrice; }

    public BigDecimal getTax() { return tax; }
    public void setTax(BigDecimal tax) { this.tax = tax; }

    public BigDecimal getDiscount() { return discount; }
    public void setDiscount(BigDecimal discount) { this.discount = discount; }

    public BigDecimal getItemSubtotal() { return itemSubtotal; }
    public void setItemSubtotal(BigDecimal itemSubtotal) { this.itemSubtotal = itemSubtotal; }

    public BigDecimal getItemTotal() { return itemTotal; }
    public void setItemTotal(BigDecimal itemTotal) { this.itemTotal = itemTotal; }

    public ItemStatus getStatus() { return status; }
    public void setStatus(ItemStatus status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }

    public Long getPurchaseOrderId() { return purchaseOrderId; }
    public void setPurchaseOrderId(Long purchaseOrderId) { this.purchaseOrderId = purchaseOrderId; }
}