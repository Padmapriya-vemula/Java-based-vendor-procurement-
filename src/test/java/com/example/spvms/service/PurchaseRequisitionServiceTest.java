package com.example.spvms.service;

import com.example.spvms.dto.RequisitionCreateDto;
import com.example.spvms.enums.RequisitionStatus;
import com.example.spvms.model.PurchaseRequisition;
import com.example.spvms.model.User;
import com.example.spvms.repository.PurchaseRequisitionRepository;
import com.example.spvms.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PurchaseRequisitionServiceTest {

    @Mock
    private PurchaseRequisitionRepository repository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private PurchaseRequisitionService service;

    @Test
    void create_Success() {
        RequisitionCreateDto dto = new RequisitionCreateDto();
        dto.setRequisitionNumber("REQ-001");
        dto.setVendorId(1L);
        dto.setTotalAmount(BigDecimal.valueOf(1000));
        dto.setQuantity(10);
        dto.setDescription("Test");
        dto.setRequisitionDate(LocalDate.now());

        User user = new User();
        user.setId(1L);
        user.setEmail("user@example.com");

        PurchaseRequisition saved = new PurchaseRequisition();
        saved.setId(1L);

        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));
        when(repository.existsByRequisitionNumber("REQ-001")).thenReturn(false);
        when(repository.save(any(PurchaseRequisition.class))).thenReturn(saved);

        PurchaseRequisition result = service.create(dto, "user@example.com");

        assertNotNull(result);
        verify(repository).save(any(PurchaseRequisition.class));
    }

    @Test
    void create_DuplicateNumber_ThrowsException() {
        RequisitionCreateDto dto = new RequisitionCreateDto();
        dto.setRequisitionNumber("REQ-001");

        User user = new User();
        user.setId(1L);

        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));
        when(repository.existsByRequisitionNumber("REQ-001")).thenReturn(true);

        assertThrows(ResponseStatusException.class, 
            () -> service.create(dto, "user@example.com"));
    }

    @Test
    void create_UserNotFound_ThrowsException() {
        RequisitionCreateDto dto = new RequisitionCreateDto();

        when(userRepository.findByEmail("invalid@example.com")).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class, 
            () -> service.create(dto, "invalid@example.com"));
    }

    @Test
    void getAll_Success() {
        List<PurchaseRequisition> requisitions = Arrays.asList(
            new PurchaseRequisition(),
            new PurchaseRequisition()
        );

        when(repository.findAll()).thenReturn(requisitions);

        List<PurchaseRequisition> result = service.getAll();

        assertEquals(2, result.size());
        verify(repository).findAll();
    }

    @Test
    void getById_Found() {
        Long id = 1L;
        PurchaseRequisition requisition = new PurchaseRequisition();
        requisition.setId(id);

        when(repository.findById(id)).thenReturn(Optional.of(requisition));

        PurchaseRequisition result = service.getById(id);

        assertNotNull(result);
        assertEquals(id, result.getId());
    }

    @Test
    void getById_NotFound_ThrowsException() {
        Long id = 999L;

        when(repository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class, () -> service.getById(id));
    }

    @Test
    void update_Success() {
        Long id = 1L;
        RequisitionCreateDto dto = new RequisitionCreateDto();
        dto.setVendorId(2L);
        dto.setTotalAmount(BigDecimal.valueOf(2000));
        dto.setQuantity(20);
        dto.setDescription("Updated");
        dto.setRequisitionDate(LocalDate.now());

        User user = new User();
        user.setId(1L);

        PurchaseRequisition existing = new PurchaseRequisition();
        existing.setId(id);
        existing.setRequesterId(1L);
        existing.setStatus(RequisitionStatus.DRAFT);

        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));
        when(repository.findById(id)).thenReturn(Optional.of(existing));
        when(repository.save(any(PurchaseRequisition.class))).thenReturn(existing);

        PurchaseRequisition result = service.update(id, dto, "user@example.com");

        assertNotNull(result);
        verify(repository).save(existing);
    }

    @Test
    void update_NotOwner_ThrowsException() {
        Long id = 1L;
        RequisitionCreateDto dto = new RequisitionCreateDto();

        User user = new User();
        user.setId(1L);

        PurchaseRequisition existing = new PurchaseRequisition();
        existing.setRequesterId(2L);
        existing.setStatus(RequisitionStatus.DRAFT);

        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));
        when(repository.findById(id)).thenReturn(Optional.of(existing));

        assertThrows(ResponseStatusException.class, 
            () -> service.update(id, dto, "user@example.com"));
    }

    @Test
    void update_NotDraft_ThrowsException() {
        Long id = 1L;
        RequisitionCreateDto dto = new RequisitionCreateDto();

        User user = new User();
        user.setId(1L);

        PurchaseRequisition existing = new PurchaseRequisition();
        existing.setRequesterId(1L);
        existing.setStatus(RequisitionStatus.SUBMITTED);

        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));
        when(repository.findById(id)).thenReturn(Optional.of(existing));

        assertThrows(ResponseStatusException.class, 
            () -> service.update(id, dto, "user@example.com"));
    }

    @Test
    void delete_Success() {
        Long id = 1L;

        when(repository.existsById(id)).thenReturn(true);
        doNothing().when(repository).deleteById(id);

        service.delete(id);

        verify(repository).deleteById(id);
    }

    @Test
    void delete_NotFound_ThrowsException() {
        Long id = 999L;

        when(repository.existsById(id)).thenReturn(false);

        assertThrows(ResponseStatusException.class, () -> service.delete(id));
        verify(repository, never()).deleteById(id);
    }
}
