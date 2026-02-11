package com.example.spvms.repository;

import com.example.spvms.model.ApprovalHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ApprovalHistoryRepository
        extends JpaRepository<ApprovalHistory, Long> {
    
    List<ApprovalHistory> findByRequisitionIdOrderByActionAtDesc(Long requisitionId);
}
