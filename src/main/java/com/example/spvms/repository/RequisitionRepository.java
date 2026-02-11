package com.example.spvms.requisition.repository;

import com.example.spvms.requisition.model.Requisition;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RequisitionRepository extends JpaRepository<Requisition, Long> {
}
