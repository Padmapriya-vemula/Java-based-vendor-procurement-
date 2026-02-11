package com.example.spvms.service;

import com.example.spvms.dto.ApprovalRequestDto;
import com.example.spvms.enums.RequisitionStatus;
import com.example.spvms.model.ApprovalHistory;
import com.example.spvms.model.PurchaseRequisition;
import com.example.spvms.model.Role;
import com.example.spvms.model.User;
import com.example.spvms.repository.ApprovalHistoryRepository;
import com.example.spvms.repository.PurchaseRequisitionRepository;
import com.example.spvms.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ApprovalServiceTest {

    @Mock
    private PurchaseRequisitionRepository requisitionRepository;

    @Mock
    private ApprovalHistoryRepository approvalHistoryRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ApprovalService approvalService;

    @Test
    void submitRequisition_Success() {
        Long requisitionId = 1L;
        String email = "user@example.com";

        User user = new User();
        user.setId(1L);
        user.setEmail(email);

        PurchaseRequisition requisition = new PurchaseRequisition();
        requisition.setId(requisitionId);
        requisition.setRequesterId(1L);
        requisition.setStatus(RequisitionStatus.DRAFT);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(requisitionRepository.findById(requisitionId)).thenReturn(Optional.of(requisition));
        when(requisitionRepository.save(any(PurchaseRequisition.class))).thenReturn(requisition);

        approvalService.submitRequisition(requisitionId, email);

        assertEquals(RequisitionStatus.SUBMITTED, requisition.getStatus());
        verify(requisitionRepository).save(requisition);
    }

    @Test
    void submitRequisition_NotDraft_ThrowsException() {
        Long requisitionId = 1L;
        String email = "user@example.com";

        PurchaseRequisition requisition = new PurchaseRequisition();
        requisition.setStatus(RequisitionStatus.SUBMITTED);

        when(requisitionRepository.findById(requisitionId)).thenReturn(Optional.of(requisition));

        assertThrows(RuntimeException.class, 
            () -> approvalService.submitRequisition(requisitionId, email));
    }

    @Test
    void submitRequisition_NotOwner_ThrowsException() {
        Long requisitionId = 1L;
        String email = "user@example.com";

        User user = new User();
        user.setId(1L);

        PurchaseRequisition requisition = new PurchaseRequisition();
        requisition.setRequesterId(2L);
        requisition.setStatus(RequisitionStatus.DRAFT);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(requisitionRepository.findById(requisitionId)).thenReturn(Optional.of(requisition));

        assertThrows(RuntimeException.class, 
            () -> approvalService.submitRequisition(requisitionId, email));
    }

    @Test
    void approveRequisition_Success() {
        Long requisitionId = 1L;
        String approverEmail = "approver@example.com";
        ApprovalRequestDto request = new ApprovalRequestDto();
        request.setComment("Approved");

        User approver = new User();
        approver.setId(2L);
        Role role = new Role();
        role.setName("PROCUREMENT");
        approver.setRoles(new HashSet<>(Arrays.asList(role)));

        PurchaseRequisition requisition = new PurchaseRequisition();
        requisition.setId(requisitionId);
        requisition.setRequesterId(1L);
        requisition.setStatus(RequisitionStatus.SUBMITTED);

        when(userRepository.findByEmail(approverEmail)).thenReturn(Optional.of(approver));
        when(requisitionRepository.findById(requisitionId)).thenReturn(Optional.of(requisition));
        when(requisitionRepository.save(any(PurchaseRequisition.class))).thenReturn(requisition);
        when(approvalHistoryRepository.save(any(ApprovalHistory.class))).thenReturn(new ApprovalHistory());

        approvalService.approveRequisition(requisitionId, request, approverEmail);

        assertEquals(RequisitionStatus.APPROVED, requisition.getStatus());
        verify(approvalHistoryRepository).save(any(ApprovalHistory.class));
    }

    @Test
    void approveRequisition_NotSubmitted_ThrowsException() {
        Long requisitionId = 1L;
        ApprovalRequestDto request = new ApprovalRequestDto();

        PurchaseRequisition requisition = new PurchaseRequisition();
        requisition.setStatus(RequisitionStatus.DRAFT);

        when(requisitionRepository.findById(requisitionId)).thenReturn(Optional.of(requisition));

        assertThrows(RuntimeException.class, 
            () -> approvalService.approveRequisition(requisitionId, request, "approver@example.com"));
    }

    @Test
    void approveRequisition_SelfApproval_ThrowsException() {
        Long requisitionId = 1L;
        String approverEmail = "user@example.com";
        ApprovalRequestDto request = new ApprovalRequestDto();

        User user = new User();
        user.setId(1L);
        Role role = new Role();
        role.setName("PROCUREMENT");
        user.setRoles(new HashSet<>(Arrays.asList(role)));

        PurchaseRequisition requisition = new PurchaseRequisition();
        requisition.setRequesterId(1L);
        requisition.setStatus(RequisitionStatus.SUBMITTED);

        when(userRepository.findByEmail(approverEmail)).thenReturn(Optional.of(user));
        when(requisitionRepository.findById(requisitionId)).thenReturn(Optional.of(requisition));

        assertThrows(RuntimeException.class, 
            () -> approvalService.approveRequisition(requisitionId, request, approverEmail));
    }

    @Test
    void approveRequisition_NoPermission_ThrowsException() {
        Long requisitionId = 1L;
        String approverEmail = "user@example.com";
        ApprovalRequestDto request = new ApprovalRequestDto();

        User user = new User();
        user.setId(2L);
        Role role = new Role();
        role.setName("VENDOR");
        user.setRoles(new HashSet<>(Arrays.asList(role)));

        PurchaseRequisition requisition = new PurchaseRequisition();
        requisition.setRequesterId(1L);
        requisition.setStatus(RequisitionStatus.SUBMITTED);

        when(userRepository.findByEmail(approverEmail)).thenReturn(Optional.of(user));
        when(requisitionRepository.findById(requisitionId)).thenReturn(Optional.of(requisition));

        assertThrows(RuntimeException.class, 
            () -> approvalService.approveRequisition(requisitionId, request, approverEmail));
    }

    @Test
    void rejectRequisition_Success() {
        Long requisitionId = 1L;
        String approverEmail = "approver@example.com";
        ApprovalRequestDto request = new ApprovalRequestDto();
        request.setComment("Rejected");

        User approver = new User();
        approver.setId(2L);
        Role role = new Role();
        role.setName("FINANCE");
        approver.setRoles(new HashSet<>(Arrays.asList(role)));

        PurchaseRequisition requisition = new PurchaseRequisition();
        requisition.setId(requisitionId);
        requisition.setRequesterId(1L);
        requisition.setStatus(RequisitionStatus.SUBMITTED);

        when(userRepository.findByEmail(approverEmail)).thenReturn(Optional.of(approver));
        when(requisitionRepository.findById(requisitionId)).thenReturn(Optional.of(requisition));
        when(requisitionRepository.save(any(PurchaseRequisition.class))).thenReturn(requisition);
        when(approvalHistoryRepository.save(any(ApprovalHistory.class))).thenReturn(new ApprovalHistory());

        approvalService.rejectRequisition(requisitionId, request, approverEmail);

        assertEquals(RequisitionStatus.REJECTED, requisition.getStatus());
        verify(approvalHistoryRepository).save(any(ApprovalHistory.class));
    }

    @Test
    void getPendingApprovals_Success() {
        List<PurchaseRequisition> pending = Arrays.asList(
            new PurchaseRequisition(),
            new PurchaseRequisition()
        );

        when(requisitionRepository.findByStatus(RequisitionStatus.SUBMITTED)).thenReturn(pending);

        List<PurchaseRequisition> result = approvalService.getPendingApprovals();

        assertEquals(2, result.size());
        verify(requisitionRepository).findByStatus(RequisitionStatus.SUBMITTED);
    }

    @Test
    void getApprovalHistory_Success() {
        Long requisitionId = 1L;
        List<ApprovalHistory> history = Arrays.asList(
            new ApprovalHistory(),
            new ApprovalHistory()
        );

        when(approvalHistoryRepository.findByRequisitionIdOrderByActionAtDesc(requisitionId))
            .thenReturn(history);

        List<ApprovalHistory> result = approvalService.getApprovalHistory(requisitionId);

        assertEquals(2, result.size());
        verify(approvalHistoryRepository).findByRequisitionIdOrderByActionAtDesc(requisitionId);
    }
}
