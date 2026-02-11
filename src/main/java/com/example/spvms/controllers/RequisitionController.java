package com.example.spvms.controllers;

import com.example.spvms.enums.RequisitionStatus;
import com.example.spvms.model.PRHistory;
import com.example.spvms.model.PurchaseRequisition;
import com.example.spvms.repository.PurchaseRequisitionRepository;
import com.example.spvms.service.PurchaseRequisitionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/requisitions")
public class RequisitionController {

    @Autowired
    private PurchaseRequisitionRepository purchaseRequisitionRepository;

    @Autowired
    private PurchaseRequisitionService purchaseRequisitionService;

    @PostMapping
    public PurchaseRequisition createRequisition(
            @RequestParam String requisitionNumber,
            @RequestParam String description,
            @RequestParam Integer quantity,
            @RequestParam BigDecimal totalAmount) {
        
        PurchaseRequisition pr = new PurchaseRequisition();
        pr.setRequisitionNumber(requisitionNumber);
        pr.setDescription(description);
        pr.setQuantity(quantity);
        pr.setTotalAmount(totalAmount);
        pr.setRequesterId(1L);
        pr.setVendorId(null); // PR does not require vendor
        pr.setStatus(RequisitionStatus.DRAFT);
        pr.setRequisitionDate(LocalDate.now());
        
        return purchaseRequisitionRepository.save(pr);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRequisition(@PathVariable Long id) {
        if (!purchaseRequisitionRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        purchaseRequisitionRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/history")
    public ResponseEntity<List<PRHistory>> getPRHistory(@PathVariable Long id) {
        return ResponseEntity.ok(purchaseRequisitionService.getHistory(id));
    }
}
