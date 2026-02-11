package com.example.spvms.service;

import com.example.spvms.dto.ApprovalRequestDto;
import com.example.spvms.enums.RequisitionStatus;
import com.example.spvms.model.ApprovalHistory;
import com.example.spvms.model.PurchaseRequisition;
import com.example.spvms.model.User;
import com.example.spvms.repository.ApprovalHistoryRepository;
import com.example.spvms.repository.PurchaseRequisitionRepository;
import com.example.spvms.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ApprovalService {

    @Autowired
    private PurchaseRequisitionRepository requisitionRepository;

    @Autowired
    private ApprovalHistoryRepository approvalHistoryRepository;

    @Autowired
    private UserRepository userRepository;

    public void submitRequisition(Long requisitionId, String email) {
        PurchaseRequisition requisition = getRequisition(requisitionId);
        
        if (requisition.getStatus() != RequisitionStatus.DRAFT) {
            throw new RuntimeException("Only draft requisitions can be submitted");
        }

        // Check if user owns the requisition
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        if (!requisition.getRequesterId().equals(user.getId())) {
            throw new RuntimeException("You can only submit your own requisitions");
        }

        requisition.setStatus(RequisitionStatus.SUBMITTED);
        requisitionRepository.save(requisition);
    }

    public void approveRequisition(Long requisitionId, ApprovalRequestDto request, String approverEmail) {
        PurchaseRequisition requisition = getRequisition(requisitionId);
        
        if (requisition.getStatus() != RequisitionStatus.SUBMITTED) {
            throw new RuntimeException("Only submitted requisitions can be approved");
        }

        // Check if user has approval authority
        validateApprovalAuthority(approverEmail, requisition);

        requisition.setStatus(RequisitionStatus.APPROVED);
        requisitionRepository.save(requisition);

        recordApprovalHistory(requisitionId, "APPROVED", request.getComment(), approverEmail);
    }

    public void rejectRequisition(Long requisitionId, ApprovalRequestDto request, String approverEmail) {
        PurchaseRequisition requisition = getRequisition(requisitionId);
        
        if (requisition.getStatus() != RequisitionStatus.SUBMITTED) {
            throw new RuntimeException("Only submitted requisitions can be rejected");
        }

        // Check if user has approval authority
        validateApprovalAuthority(approverEmail, requisition);

        requisition.setStatus(RequisitionStatus.REJECTED);
        requisitionRepository.save(requisition);

        recordApprovalHistory(requisitionId, "REJECTED", request.getComment(), approverEmail);
    }

    public List<ApprovalHistory> getApprovalHistory(Long requisitionId) {
        return approvalHistoryRepository.findByRequisitionIdOrderByActionAtDesc(requisitionId);
    }

    public List<PurchaseRequisition> getPendingApprovals() {
        return requisitionRepository.findByStatus(RequisitionStatus.SUBMITTED);
    }

    private PurchaseRequisition getRequisition(Long requisitionId) {
        return requisitionRepository.findById(requisitionId)
                .orElseThrow(() -> new RuntimeException("Requisition not found"));
    }

    private void validateApprovalAuthority(String email, PurchaseRequisition requisition) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Check if user is trying to approve their own requisition
        if (requisition.getRequesterId().equals(user.getId())) {
            throw new RuntimeException("You cannot approve your own requisition");
        }

        // Check if user has PROCUREMENT, FINANCE, or ADMIN role
        boolean hasApprovalRole = user.getRoles().stream()
                .anyMatch(role -> role.getName().equals("PROCUREMENT") || 
                                role.getName().equals("FINANCE") || 
                                role.getName().equals("ADMIN"));

        if (!hasApprovalRole) {
            throw new RuntimeException("You do not have permission to approve requisitions");
        }
    }

    private void recordApprovalHistory(Long requisitionId, String action, String comment, String actionBy) {
        ApprovalHistory history = new ApprovalHistory();
        history.setRequisitionId(requisitionId);
        history.setAction(action);
        history.setComment(comment);
        history.setActionBy(actionBy);
        history.setActionAt(LocalDateTime.now());
        approvalHistoryRepository.save(history);
    }
}