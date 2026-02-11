package com.example.spvms.repository;

import com.example.spvms.enums.RequisitionStatus;
import com.example.spvms.model.PurchaseRequisition;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PurchaseRequisitionRepository
        extends JpaRepository<PurchaseRequisition, Long> {

    boolean existsByPrNumber(String prNumber);

    boolean existsByRequisitionNumber(String requisitionNumber);
    
    List<PurchaseRequisition> findByStatus(RequisitionStatus status);
}
