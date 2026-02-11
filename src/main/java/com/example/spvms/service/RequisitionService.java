package com.example.spvms.requisition.service;

import com.example.spvms.requisition.model.Requisition;
import com.example.spvms.requisition.model.RequisitionHistory;
import com.example.spvms.requisition.repository.RequisitionRepository;
import com.example.spvms.requisition.repository.RequisitionHistoryRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class RequisitionService {

    private final RequisitionRepository requisitionRepository;
    private final RequisitionHistoryRepository historyRepository;

    public RequisitionService(RequisitionRepository requisitionRepository,
                              RequisitionHistoryRepository historyRepository) {
        this.requisitionRepository = requisitionRepository;
        this.historyRepository = historyRepository;
    }

    public Requisition createRequisition(Requisition requisition) {
        requisition.setStatus("DRAFT");
        requisition.setCreatedAt(LocalDateTime.now());
        requisition.setUpdatedAt(LocalDateTime.now());

        Requisition saved = requisitionRepository.save(requisition);
        saveHistory(saved, "DRAFT", null, requisition.getCreatedBy());
        return saved;
    }

    public Requisition submitRequisition(Long id, String comments, String user) {
        Requisition requisition = requisitionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Requisition not found"));

        requisition.setStatus("SUBMITTED");
        requisition.setUpdatedAt(LocalDateTime.now());
        requisitionRepository.save(requisition);

        saveHistory(requisition, "SUBMITTED", comments, user);
        return requisition;
    }

    public Requisition approveRequisition(Long id, String comments, String user) {
        Requisition requisition = requisitionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Requisition not found"));

        requisition.setStatus("APPROVED");
        requisition.setUpdatedAt(LocalDateTime.now());
        requisitionRepository.save(requisition);

        saveHistory(requisition, "APPROVED", comments, user);
        return requisition;
    }

    public Requisition rejectRequisition(Long id, String comments, String user) {
        Requisition requisition = requisitionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Requisition not found"));

        requisition.setStatus("REJECTED");
        requisition.setUpdatedAt(LocalDateTime.now());
        requisitionRepository.save(requisition);

        saveHistory(requisition, "REJECTED", comments, user);
        return requisition;
    }

    public List<RequisitionHistory> getHistory(Long requisitionId) {
        return historyRepository.findByRequisitionIdOrderByChangedAtAsc(requisitionId);
    }

    private void saveHistory(Requisition requisition, String status, String comments, String user) {
        RequisitionHistory history = new RequisitionHistory();
        history.setRequisitionId(requisition.getId());
        history.setStatus(status);
        history.setComments(comments);
        history.setChangedBy(user);
        history.setChangedAt(LocalDateTime.now());

        historyRepository.save(history);
    }
}
