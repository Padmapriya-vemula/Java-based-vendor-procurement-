package com.example.spvms.controllers;

import com.example.spvms.enums.POStatus;
import com.example.spvms.repository.PurchaseOrderRepository;
import com.example.spvms.repository.VendorRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/stats")
public class StatsController {

    private final VendorRepository vendorRepository;
    private final PurchaseOrderRepository purchaseOrderRepository;

    public StatsController(VendorRepository vendorRepository, PurchaseOrderRepository purchaseOrderRepository) {
        this.vendorRepository = vendorRepository;
        this.purchaseOrderRepository = purchaseOrderRepository;
    }

    @GetMapping
    public Map<String, Object> getStats() {
        Map<String, Object> stats = new HashMap<>();
        
        stats.put("activeVendors", vendorRepository.count());
        stats.put("pendingOrders", purchaseOrderRepository.countByStatus(POStatus.OPEN));
        stats.put("pendingApprovals", 0);
        
        LocalDateTime startOfMonth = LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
        BigDecimal monthlySpend = purchaseOrderRepository.findAll().stream()
            .filter(po -> po.getCreatedAt() != null && po.getCreatedAt().isAfter(startOfMonth))
            .map(po -> po.getTotalAmount() != null ? po.getTotalAmount() : BigDecimal.ZERO)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        stats.put("monthlySpend", monthlySpend);
        
        return stats;
    }
}
