package com.example.spvms.repository;

import com.example.spvms.enums.ItemStatus;
import com.example.spvms.model.PurchaseOrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PurchaseOrderItemRepository
        extends JpaRepository<PurchaseOrderItem, Long> {

    List<PurchaseOrderItem> findByStatus(ItemStatus status);
    List<PurchaseOrderItem> findByPurchaseOrderId(Long purchaseOrderId);
}
