package com.example.spvms.requisition.repository;

import com.example.spvms.requisition.model.RequisitionHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RequisitionHistoryRepository extends JpaRepository<RequisitionHistory, Long> {
    List<RequisitionHistory> findByRequisitionIdOrderByChangedAtAsc(Long requisitionId);
}
