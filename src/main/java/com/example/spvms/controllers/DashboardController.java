package com.example.spvms.controllers;

import com.example.spvms.dto.*;
import com.example.spvms.service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/dashboard")
@CrossOrigin(origins = "*")
public class DashboardController {

    @Autowired
    private DashboardService dashboardService;

    @GetMapping("/overview")
    public ResponseEntity<DashboardOverviewDto> getOverview() {
        return ResponseEntity.ok(dashboardService.getOverview());
    }

    @GetMapping("/pr-status")
    public ResponseEntity<List<PRStatusDto>> getPRStatus() {
        return ResponseEntity.ok(dashboardService.getPRStatusDistribution());
    }

    @GetMapping("/po-status")
    public ResponseEntity<List<POStatusDto>> getPOStatus() {
        return ResponseEntity.ok(dashboardService.getPOStatusDistribution());
    }

    @GetMapping("/monthly-trend")
    public ResponseEntity<List<MonthlyTrendDto>> getMonthlyTrend() {
        return ResponseEntity.ok(dashboardService.getMonthlyTrend());
    }

    @GetMapping("/vendor-po-values")
    public ResponseEntity<List<VendorPOValueDto>> getVendorPOValues() {
        return ResponseEntity.ok(dashboardService.getVendorPOValues());
    }
}
