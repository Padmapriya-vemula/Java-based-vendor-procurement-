package com.example.spvms.requisition.service;

import com.example.spvms.requisition.model.Requisition;
import com.example.spvms.requisition.model.RequisitionHistory;
import com.example.spvms.requisition.repository.RequisitionHistoryRepository;
import com.example.spvms.requisition.repository.RequisitionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RequisitionServiceTest {

    @Mock
    private RequisitionRepository requisitionRepository;

    @Mock
    private RequisitionHistoryRepository historyRepository;

    @InjectMocks
    private RequisitionService requisitionService;

    @Test
    void createRequisition_Success() {
        Requisition requisition = new Requisition();
        requisition.setCreatedBy("user1");

        Requisition saved = new Requisition();
        saved.setId(1L);
        saved.setStatus("DRAFT");

        when(requisitionRepository.save(any(Requisition.class))).thenReturn(saved);
        when(historyRepository.save(any(RequisitionHistory.class))).thenReturn(new RequisitionHistory());

        Requisition result = requisitionService.createRequisition(requisition);

        assertNotNull(result);
        assertEquals("DRAFT", result.getStatus());
        verify(requisitionRepository).save(any(Requisition.class));
        verify(historyRepository).save(any(RequisitionHistory.class));
    }

    @Test
    void submitRequisition_Success() {
        Long id = 1L;
        Requisition requisition = new Requisition();
        requisition.setId(id);
        requisition.setStatus("DRAFT");

        when(requisitionRepository.findById(id)).thenReturn(Optional.of(requisition));
        when(requisitionRepository.save(any(Requisition.class))).thenReturn(requisition);
        when(historyRepository.save(any(RequisitionHistory.class))).thenReturn(new RequisitionHistory());

        Requisition result = requisitionService.submitRequisition(id, "Submitting", "user1");

        assertEquals("SUBMITTED", result.getStatus());
        verify(requisitionRepository).save(requisition);
        verify(historyRepository).save(any(RequisitionHistory.class));
    }

    @Test
    void submitRequisition_NotFound() {
        Long id = 999L;

        when(requisitionRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, 
            () -> requisitionService.submitRequisition(id, "comment", "user"));
    }

    @Test
    void approveRequisition_Success() {
        Long id = 1L;
        Requisition requisition = new Requisition();
        requisition.setId(id);
        requisition.setStatus("SUBMITTED");

        when(requisitionRepository.findById(id)).thenReturn(Optional.of(requisition));
        when(requisitionRepository.save(any(Requisition.class))).thenReturn(requisition);
        when(historyRepository.save(any(RequisitionHistory.class))).thenReturn(new RequisitionHistory());

        Requisition result = requisitionService.approveRequisition(id, "Approved", "approver");

        assertEquals("APPROVED", result.getStatus());
        verify(historyRepository).save(any(RequisitionHistory.class));
    }

    @Test
    void rejectRequisition_Success() {
        Long id = 1L;
        Requisition requisition = new Requisition();
        requisition.setId(id);
        requisition.setStatus("SUBMITTED");

        when(requisitionRepository.findById(id)).thenReturn(Optional.of(requisition));
        when(requisitionRepository.save(any(Requisition.class))).thenReturn(requisition);
        when(historyRepository.save(any(RequisitionHistory.class))).thenReturn(new RequisitionHistory());

        Requisition result = requisitionService.rejectRequisition(id, "Rejected", "approver");

        assertEquals("REJECTED", result.getStatus());
        verify(historyRepository).save(any(RequisitionHistory.class));
    }

    @Test
    void getHistory_Success() {
        Long requisitionId = 1L;
        List<RequisitionHistory> history = Arrays.asList(
            new RequisitionHistory(),
            new RequisitionHistory()
        );

        when(historyRepository.findByRequisitionIdOrderByChangedAtAsc(requisitionId))
            .thenReturn(history);

        List<RequisitionHistory> result = requisitionService.getHistory(requisitionId);

        assertEquals(2, result.size());
        verify(historyRepository).findByRequisitionIdOrderByChangedAtAsc(requisitionId);
    }
}
