package com.example.spvms.repository;

import com.example.spvms.enums.POStatus;
import com.example.spvms.model.PurchaseOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PurchaseOrderRepository
        extends JpaRepository<PurchaseOrder, Long> {
    long countByStatus(POStatus status);
}

