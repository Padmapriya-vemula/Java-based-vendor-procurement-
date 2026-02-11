package com.example.spvms.service;

import com.example.spvms.dto.RequisitionCreateDto;
import com.example.spvms.enums.RequisitionStatus;
import com.example.spvms.model.PRHistory;
import com.example.spvms.model.PurchaseRequisition;
import com.example.spvms.model.User;
import com.example.spvms.repository.PRHistoryRepository;
import com.example.spvms.repository.PurchaseRequisitionRepository;
import com.example.spvms.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class PurchaseRequisitionService {

    private final PurchaseRequisitionRepository repository;
    private final UserRepository userRepository;
    private final PRHistoryRepository historyRepository;

    public PurchaseRequisitionService(PurchaseRequisitionRepository repository, UserRepository userRepository, PRHistoryRepository historyRepository) {
        this.repository = repository;
        this.userRepository = userRepository;
        this.historyRepository = historyRepository;
    }

    public PurchaseRequisition create(RequisitionCreateDto dto, String email) {
        // Get user ID from email
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));

        // Check if requisition number already exists
        if (repository.existsByRequisitionNumber(dto.getRequisitionNumber())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Requisition number already exists"
            );
        }

        // Create new requisition
        PurchaseRequisition pr = new PurchaseRequisition();
        pr.setRequisitionNumber(dto.getRequisitionNumber());
        pr.setRequesterId(user.getId());
        pr.setVendorId(dto.getVendorId());
        pr.setTotalAmount(dto.getTotalAmount());
        pr.setQuantity(dto.getQuantity());
        pr.setDescription(dto.getDescription());
        pr.setRequisitionDate(dto.getRequisitionDate());
        // Status and prNumber are set automatically in @PrePersist

        PurchaseRequisition saved = repository.save(pr);
        logHistory(saved.getId(), saved.getStatus(), "PR Created", user.getId(), "Purchase Requisition created");
        return saved;
    }

    public List<PurchaseRequisition> getAll() {
        return repository.findAll();
    }

    public PurchaseRequisition getById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Purchase Requisition not found with id: " + id
                ));
    }

    public PurchaseRequisition update(Long id, RequisitionCreateDto dto, String email) {
        PurchaseRequisition existing = getById(id);
        
        // Only allow updates if user owns the requisition and it's in DRAFT status
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));
        
        if (!existing.getRequesterId().equals(user.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only update your own requisitions");
        }
        
        if (existing.getStatus() != RequisitionStatus.DRAFT) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Only draft requisitions can be updated");
        }

        // Update fields
        existing.setVendorId(dto.getVendorId());
        existing.setTotalAmount(dto.getTotalAmount());
        existing.setQuantity(dto.getQuantity());
        existing.setDescription(dto.getDescription());
        existing.setRequisitionDate(dto.getRequisitionDate());

        PurchaseRequisition updated = repository.save(existing);
        logHistory(updated.getId(), updated.getStatus(), "PR Updated", user.getId(), "Purchase Requisition updated");
        return updated;
    }

    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Purchase Requisition not found with id: " + id
            );
        }
        repository.deleteById(id);
    }

    public List<PRHistory> getHistory(Long prId) {
        return historyRepository.findByPrIdOrderByCreatedAtDesc(prId);
    }

    private void logHistory(Long prId, RequisitionStatus status, String action, Long changedBy, String remarks) {
        PRHistory history = new PRHistory();
        history.setPrId(prId);
        history.setStatus(status);
        history.setAction(action);
        history.setChangedBy(changedBy);
        history.setRemarks(remarks);
        historyRepository.save(history);
    }
}
