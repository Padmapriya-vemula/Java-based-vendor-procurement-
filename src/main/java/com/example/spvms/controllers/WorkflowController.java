package com.example.spvms.controllers;

import com.example.spvms.enums.RequisitionStatus;
import com.example.spvms.model.PurchaseRequisition;
import com.example.spvms.repository.PurchaseRequisitionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/workflow")
public class WorkflowController {

    @Autowired
    private PurchaseRequisitionRepository purchaseRequisitionRepository;

    @PostMapping("/requisitions/{id}/submit")
    public PurchaseRequisition submitRequisition(@PathVariable Long id) {
        PurchaseRequisition pr = purchaseRequisitionRepository.findById(id).orElseThrow();
        pr.setStatus(RequisitionStatus.SUBMITTED);
        return purchaseRequisitionRepository.save(pr);
    }

    @PostMapping("/requisitions/{id}/approve")
    public PurchaseRequisition approveRequisition(@PathVariable Long id, @RequestBody(required = false) Object comment) {
        PurchaseRequisition pr = purchaseRequisitionRepository.findById(id).orElseThrow();
        pr.setStatus(RequisitionStatus.APPROVED);
        return purchaseRequisitionRepository.save(pr);
    }

    @PostMapping("/requisitions/{id}/reject")
    public PurchaseRequisition rejectRequisition(@PathVariable Long id, @RequestBody(required = false) Object comment) {
        PurchaseRequisition pr = purchaseRequisitionRepository.findById(id).orElseThrow();
        pr.setStatus(RequisitionStatus.REJECTED);
        return purchaseRequisitionRepository.save(pr);
    }

    @GetMapping("/requisitions/pending")
    public List<PurchaseRequisition> getPendingRequisitions() {
        return purchaseRequisitionRepository.findByStatus(RequisitionStatus.SUBMITTED);
    }
}