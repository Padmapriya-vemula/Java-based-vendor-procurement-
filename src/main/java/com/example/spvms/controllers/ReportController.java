package com.example.spvms.controllers;

import com.example.spvms.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    @Autowired
    private ReportService reportService;

    @GetMapping("/vendor")
    public ResponseEntity<byte[]> generateVendorReport(
            @RequestParam(defaultValue = "pdf") String format) {
        
        try {
            byte[] reportData = reportService.generateVendorReport(format, null, null, null);
            return buildResponse(reportData, format, "vendor_report");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/pr")
    public ResponseEntity<byte[]> generatePRReport(
            @RequestParam(defaultValue = "pdf") String format) {
        
        try {
            byte[] reportData = reportService.generatePRReport(format, null, null, null);
            return buildResponse(reportData, format, "pr_report");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/po")
    public ResponseEntity<byte[]> generatePOReport(
            @RequestParam(defaultValue = "pdf") String format) {
        
        try {
            byte[] reportData = reportService.generatePOReport(format, null, null, null);
            return buildResponse(reportData, format, "po_report");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    private ResponseEntity<byte[]> buildResponse(byte[] content, String format, String filename) {
        HttpHeaders headers = new HttpHeaders();
        
        if ("excel".equalsIgnoreCase(format)) {
            headers.setContentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
            headers.setContentDispositionFormData("attachment", filename + ".xlsx");
        } else {
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", filename + ".pdf");
        }
        
        return ResponseEntity.ok()
                .headers(headers)
                .body(content);
    }
}