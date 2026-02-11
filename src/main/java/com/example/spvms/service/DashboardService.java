package com.example.spvms.service;

import com.example.spvms.dto.*;
import com.example.spvms.repository.PurchaseOrderRepository;
import com.example.spvms.repository.PurchaseRequisitionRepository;
import com.example.spvms.repository.VendorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class DashboardService {

    @Autowired
    private VendorRepository vendorRepository;

    @Autowired
    private PurchaseRequisitionRepository prRepository;

    @Autowired
    private PurchaseOrderRepository poRepository;

    public DashboardOverviewDto getOverview() {
        Long totalVendors = vendorRepository.count();
        Long activeVendors = vendorRepository.findAll().stream()
                .filter(v -> v.getCompliance() != null && v.getCompliance())
                .count();
        Long inactiveVendors = totalVendors - activeVendors;
        Long totalPRs = prRepository.count();
        Long totalPOs = poRepository.count();
        BigDecimal totalAmount = poRepository.findAll().stream()
                .map(po -> po.getTotalAmount() != null ? po.getTotalAmount() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new DashboardOverviewDto(totalVendors, activeVendors, inactiveVendors,
                totalPRs, totalPOs, totalAmount);
    }

    public List<PRStatusDto> getPRStatusDistribution() {
        Map<String, Long> statusMap = prRepository.findAll().stream()
                .collect(Collectors.groupingBy(
                        pr -> pr.getStatus().toString(),
                        Collectors.counting()
                ));

        return statusMap.entrySet().stream()
                .map(e -> new PRStatusDto(e.getKey(), e.getValue()))
                .collect(Collectors.toList());
    }

    public List<POStatusDto> getPOStatusDistribution() {
        Map<String, Long> statusMap = poRepository.findAll().stream()
                .collect(Collectors.groupingBy(
                        po -> po.getStatus().toString(),
                        Collectors.counting()
                ));

        return statusMap.entrySet().stream()
                .map(e -> new POStatusDto(e.getKey(), e.getValue()))
                .collect(Collectors.toList());
    }

    public List<MonthlyTrendDto> getMonthlyTrend() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM yyyy");
        LocalDateTime sixMonthsAgo = LocalDateTime.now().minusMonths(6);

        Map<String, Long> prByMonth = prRepository.findAll().stream()
                .filter(pr -> pr.getCreatedAt() != null && pr.getCreatedAt().isAfter(sixMonthsAgo))
                .collect(Collectors.groupingBy(
                        pr -> pr.getCreatedAt().format(formatter),
                        Collectors.counting()
                ));

        Map<String, Long> poByMonth = poRepository.findAll().stream()
                .filter(po -> po.getCreatedAt() != null && po.getCreatedAt().isAfter(sixMonthsAgo))
                .collect(Collectors.groupingBy(
                        po -> po.getCreatedAt().format(formatter),
                        Collectors.counting()
                ));

        Map<String, BigDecimal> spendingByMonth = poRepository.findAll().stream()
                .filter(po -> po.getCreatedAt() != null && po.getCreatedAt().isAfter(sixMonthsAgo))
                .collect(Collectors.groupingBy(
                        po -> po.getCreatedAt().format(formatter),
                        Collectors.reducing(BigDecimal.ZERO,
                                po -> po.getTotalAmount() != null ? po.getTotalAmount() : BigDecimal.ZERO,
                                BigDecimal::add)
                ));

        Set<String> allMonths = new TreeSet<>();
        allMonths.addAll(prByMonth.keySet());
        allMonths.addAll(poByMonth.keySet());
        allMonths.addAll(spendingByMonth.keySet());

        return allMonths.stream()
                .map(month -> new MonthlyTrendDto(
                        month,
                        prByMonth.getOrDefault(month, 0L),
                        poByMonth.getOrDefault(month, 0L),
                        spendingByMonth.getOrDefault(month, BigDecimal.ZERO)
                ))
                .collect(Collectors.toList());
    }

    public List<VendorPOValueDto> getVendorPOValues() {
        Map<Long, BigDecimal> vendorValues = poRepository.findAll().stream()
                .filter(po -> po.getVendorId() != null)
                .collect(Collectors.groupingBy(
                        po -> po.getVendorId(),
                        Collectors.reducing(BigDecimal.ZERO,
                                po -> po.getTotalAmount() != null ? po.getTotalAmount() : BigDecimal.ZERO,
                                BigDecimal::add)
                ));

        return vendorValues.entrySet().stream()
                .map(e -> {
                    String vendorName = vendorRepository.findById(e.getKey())
                            .map(v -> v.getName())
                            .orElse("Unknown Vendor");
                    return new VendorPOValueDto(e.getKey(), vendorName, e.getValue());
                })
                .sorted((a, b) -> b.getTotalValue().compareTo(a.getTotalValue()))
                .limit(10)
                .collect(Collectors.toList());
    }
}
