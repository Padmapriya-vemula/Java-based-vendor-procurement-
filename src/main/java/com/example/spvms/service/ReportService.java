package com.example.spvms.service;

import com.example.spvms.enums.RequisitionStatus;
import com.example.spvms.model.PurchaseOrder;
import com.example.spvms.model.PurchaseRequisition;
import com.example.spvms.model.Vendor;
import com.example.spvms.repository.PurchaseOrderRepository;
import com.example.spvms.repository.PurchaseRequisitionRepository;
import com.example.spvms.repository.VendorRepository;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReportService {

    @Autowired
    private VendorRepository vendorRepository;

    @Autowired
    private PurchaseRequisitionRepository purchaseRequisitionRepository;

    @Autowired
    private PurchaseOrderRepository purchaseOrderRepository;

    public byte[] generateVendorReport(String format, Double minRating, String location, String category) throws Exception {
        List<Vendor> vendors = vendorRepository.findAll();
        
        // Apply filters
        if (minRating != null) {
            vendors = vendors.stream()
                    .filter(v -> v.getRating() != null && v.getRating() >= minRating)
                    .collect(Collectors.toList());
        }
        if (location != null && !location.isEmpty()) {
            vendors = vendors.stream()
                    .filter(v -> v.getLocation() != null && v.getLocation().toLowerCase().contains(location.toLowerCase()))
                    .collect(Collectors.toList());
        }
        if (category != null && !category.isEmpty()) {
            vendors = vendors.stream()
                    .filter(v -> category.equals(v.getCategory()))
                    .collect(Collectors.toList());
        }

        return generateVendorReportContent(vendors, format);
    }

    public byte[] generatePRReport(String format, LocalDate startDate, LocalDate endDate, RequisitionStatus status) throws Exception {
        List<PurchaseRequisition> prs = purchaseRequisitionRepository.findAll();
        
        // Apply filters
        if (startDate != null && endDate != null) {
            prs = prs.stream()
                    .filter(pr -> pr.getCreatedAt() != null && 
                            !pr.getCreatedAt().toLocalDate().isBefore(startDate) && 
                            !pr.getCreatedAt().toLocalDate().isAfter(endDate))
                    .collect(Collectors.toList());
        }
        if (status != null) {
            prs = prs.stream()
                    .filter(pr -> status.equals(pr.getStatus()))
                    .collect(Collectors.toList());
        }

        return generatePRReportContent(prs, format);
    }

    public byte[] generatePOReport(String format, LocalDate startDate, LocalDate endDate, String vendorName) throws Exception {
        List<PurchaseOrder> pos = purchaseOrderRepository.findAll();
        
        // Apply filters
        if (startDate != null && endDate != null) {
            pos = pos.stream()
                    .filter(po -> po.getCreatedAt() != null && 
                            !po.getCreatedAt().toLocalDate().isBefore(startDate) && 
                            !po.getCreatedAt().toLocalDate().isAfter(endDate))
                    .collect(Collectors.toList());
        }

        return generatePOReportContent(pos, format);
    }

    private byte[] generateVendorReportContent(List<Vendor> vendors, String format) {
        if ("excel".equalsIgnoreCase(format)) {
            return generateVendorExcel(vendors);
        } else {
            return generateVendorPDF(vendors);
        }
    }

    private byte[] generateVendorPDF(List<Vendor> vendors) {
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            Document document = new Document(PageSize.A4);
            PdfWriter.getInstance(document, out);
            document.open();
            
            com.itextpdf.text.Font titleFont = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA, 18, com.itextpdf.text.Font.BOLD);
            com.itextpdf.text.Font headerFont = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA, 12, com.itextpdf.text.Font.BOLD);
            
            Paragraph title = new Paragraph("VENDOR REPORT", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);
            document.add(new Paragraph(" "));
            document.add(new Paragraph("Generated: " + LocalDate.now()));
            document.add(new Paragraph("Total Vendors: " + vendors.size()));
            document.add(new Paragraph(" "));
            
            PdfPTable table = new PdfPTable(8);
            table.setWidthPercentage(100);
            
            addTableHeader(table, new String[]{"ID", "Name", "Email", "Phone", "Location", "Category", "Rating", "Compliance"}, headerFont);
            
            for (Vendor v : vendors) {
                table.addCell(String.valueOf(v.getId()));
                table.addCell(v.getName());
                table.addCell(v.getEmail());
                table.addCell(v.getPhone());
                table.addCell(v.getLocation());
                table.addCell(v.getCategory());
                table.addCell(String.valueOf(v.getRating()));
                table.addCell(v.getCompliance() ? "Yes" : "No");
            }
            
            document.add(table);
            document.close();
            return out.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
            return ("Error generating PDF: " + e.getMessage()).getBytes();
        }
    }

    private byte[] generateVendorExcel(List<Vendor> vendors) {
        try {
            Workbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("Vendors");
            
            Row headerRow = sheet.createRow(0);
            String[] headers = {"ID", "Name", "Email", "Phone", "Location", "Category", "Rating", "Compliance"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
            }
            
            int rowNum = 1;
            for (Vendor v : vendors) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(v.getId());
                row.createCell(1).setCellValue(v.getName());
                row.createCell(2).setCellValue(v.getEmail());
                row.createCell(3).setCellValue(v.getPhone());
                row.createCell(4).setCellValue(v.getLocation());
                row.createCell(5).setCellValue(v.getCategory());
                row.createCell(6).setCellValue(v.getRating());
                row.createCell(7).setCellValue(v.getCompliance() ? "Yes" : "No");
            }
            
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workbook.write(out);
            workbook.close();
            return out.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
            return ("Error generating Excel: " + e.getMessage()).getBytes();
        }
    }

    private void addTableHeader(PdfPTable table, String[] headers, com.itextpdf.text.Font font) {
        for (String header : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(header, font));
            cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);
        }
    }

    private byte[] generatePRReportContent(List<PurchaseRequisition> prs, String format) {
        if ("excel".equalsIgnoreCase(format)) {
            return generatePRExcel(prs);
        } else {
            return generatePRPDF(prs);
        }
    }

    private byte[] generatePRPDF(List<PurchaseRequisition> prs) {
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            Document document = new Document(PageSize.A4);
            PdfWriter.getInstance(document, out);
            document.open();
            
            com.itextpdf.text.Font titleFont = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA, 18, com.itextpdf.text.Font.BOLD);
            com.itextpdf.text.Font headerFont = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA, 12, com.itextpdf.text.Font.BOLD);
            
            Paragraph title = new Paragraph("PURCHASE REQUISITION REPORT", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);
            document.add(new Paragraph(" "));
            document.add(new Paragraph("Generated: " + LocalDate.now()));
            document.add(new Paragraph("Total PRs: " + prs.size()));
            document.add(new Paragraph(" "));
            
            PdfPTable table = new PdfPTable(7);
            table.setWidthPercentage(100);
            
            addTableHeader(table, new String[]{"PR Number", "Req Number", "Description", "Quantity", "Amount", "Status", "Date"}, headerFont);
            
            for (PurchaseRequisition pr : prs) {
                table.addCell(pr.getPrNumber() != null ? pr.getPrNumber() : "-");
                table.addCell(pr.getRequisitionNumber() != null ? pr.getRequisitionNumber() : "-");
                table.addCell(pr.getDescription() != null ? pr.getDescription() : "-");
                table.addCell(String.valueOf(pr.getQuantity()));
                table.addCell("₹" + pr.getTotalAmount());
                table.addCell(pr.getStatus().toString());
                table.addCell(pr.getRequisitionDate() != null ? pr.getRequisitionDate().toString() : "-");
            }
            
            document.add(table);
            document.close();
            return out.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
            return ("Error generating PDF: " + e.getMessage()).getBytes();
        }
    }

    private byte[] generatePRExcel(List<PurchaseRequisition> prs) {
        try {
            Workbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("Purchase Requisitions");
            
            Row headerRow = sheet.createRow(0);
            String[] headers = {"PR Number", "Requisition Number", "Description", "Quantity", "Total Amount", "Status", "Date"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
            }
            
            int rowNum = 1;
            for (PurchaseRequisition pr : prs) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(pr.getPrNumber());
                row.createCell(1).setCellValue(pr.getRequisitionNumber());
                row.createCell(2).setCellValue(pr.getDescription());
                row.createCell(3).setCellValue(pr.getQuantity());
                row.createCell(4).setCellValue(pr.getTotalAmount().doubleValue());
                row.createCell(5).setCellValue(pr.getStatus().toString());
                row.createCell(6).setCellValue(pr.getRequisitionDate() != null ? pr.getRequisitionDate().toString() : "-");
            }
            
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workbook.write(out);
            workbook.close();
            return out.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
            return ("Error generating Excel: " + e.getMessage()).getBytes();
        }
    }

    private byte[] generatePOReportContent(List<PurchaseOrder> pos, String format) {
        if ("excel".equalsIgnoreCase(format)) {
            return generatePOExcel(pos);
        } else {
            return generatePOPDF(pos);
        }
    }

    private byte[] generatePOPDF(List<PurchaseOrder> pos) {
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            Document document = new Document(PageSize.A4);
            PdfWriter.getInstance(document, out);
            document.open();
            
            com.itextpdf.text.Font titleFont = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA, 18, com.itextpdf.text.Font.BOLD);
            com.itextpdf.text.Font headerFont = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA, 12, com.itextpdf.text.Font.BOLD);
            
            Paragraph title = new Paragraph("PURCHASE ORDER REPORT", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);
            document.add(new Paragraph(" "));
            document.add(new Paragraph("Generated: " + LocalDate.now()));
            document.add(new Paragraph("Total POs: " + pos.size()));
            document.add(new Paragraph(" "));
            
            PdfPTable table = new PdfPTable(7);
            table.setWidthPercentage(100);
            
            addTableHeader(table, new String[]{"ID", "Title", "Status", "Subtotal", "Tax", "Discount", "Total"}, headerFont);
            
            for (PurchaseOrder po : pos) {
                table.addCell(String.valueOf(po.getId()));
                table.addCell(po.getTitle() != null ? po.getTitle() : "-");
                table.addCell(po.getStatus().toString());
                table.addCell("₹" + (po.getSubtotal() != null ? po.getSubtotal() : "0"));
                table.addCell("₹" + (po.getTax() != null ? po.getTax() : "0"));
                table.addCell("₹" + (po.getDiscount() != null ? po.getDiscount() : "0"));
                table.addCell("₹" + (po.getTotalAmount() != null ? po.getTotalAmount() : "0"));
            }
            
            document.add(table);
            document.close();
            return out.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
            return ("Error generating PDF: " + e.getMessage()).getBytes();
        }
    }

    private byte[] generatePOExcel(List<PurchaseOrder> pos) {
        try {
            Workbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("Purchase Orders");
            
            Row headerRow = sheet.createRow(0);
            String[] headers = {"ID", "Title", "Status", "Subtotal", "Tax", "Discount", "Total Amount"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
            }
            
            int rowNum = 1;
            for (PurchaseOrder po : pos) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(po.getId());
                row.createCell(1).setCellValue(po.getTitle());
                row.createCell(2).setCellValue(po.getStatus().toString());
                row.createCell(3).setCellValue(po.getSubtotal() != null ? po.getSubtotal().doubleValue() : 0);
                row.createCell(4).setCellValue(po.getTax() != null ? po.getTax().doubleValue() : 0);
                row.createCell(5).setCellValue(po.getDiscount() != null ? po.getDiscount().doubleValue() : 0);
                row.createCell(6).setCellValue(po.getTotalAmount() != null ? po.getTotalAmount().doubleValue() : 0);
            }
            
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workbook.write(out);
            workbook.close();
            return out.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
            return ("Error generating Excel: " + e.getMessage()).getBytes();
        }
    }

    private byte[] generateJasperReport(String templateName, List<?> data, String reportTitle, String format) throws Exception {
        // Use fallback report generation
        return generateFallbackReport(reportTitle, data.size(), format);
    }

    private byte[] generateFallbackReport(String title, int recordCount, String format) {
        StringBuilder content = new StringBuilder();
        content.append(title).append("\n\n");
        content.append("Total Records: ").append(recordCount).append("\n");
        content.append("Generated on: ").append(LocalDate.now()).append("\n\n");
        content.append("This is a summary report.\n");
        content.append("For detailed data, please access the system directly.\n");
        
        if ("excel".equalsIgnoreCase(format)) {
            // Simple CSV format for Excel
            String csv = "Report Title," + title + "\n" +
                        "Total Records," + recordCount + "\n" +
                        "Generated Date," + LocalDate.now() + "\n";
            return csv.getBytes();
        }
        
        return content.toString().getBytes();
    }
}