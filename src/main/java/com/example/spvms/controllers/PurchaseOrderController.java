package com.example.spvms.controllers;

import com.example.spvms.dto.PurchaseOrderResponse;
import com.example.spvms.dto.PurchaseOrderItemResponse;
import com.example.spvms.enums.ItemStatus;
import com.example.spvms.enums.POStatus;
import com.example.spvms.model.POHistory;
import com.example.spvms.model.PurchaseOrder;
import com.example.spvms.model.PurchaseOrderItem;
import com.example.spvms.model.Vendor;
import com.example.spvms.repository.PurchaseOrderRepository;
import com.example.spvms.repository.VendorRepository;
import com.example.spvms.service.PurchaseOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/purchase-orders")
public class PurchaseOrderController {

    @Autowired
    private PurchaseOrderRepository purchaseOrderRepository;
    
    @Autowired
    private PurchaseOrderService purchaseOrderService;
    
    @Autowired
    private VendorRepository vendorRepository;

    // Create PO
    @PostMapping
    public PurchaseOrderResponse createPurchaseOrder(
            @RequestParam String title,
            @RequestParam Long vendorId) {
        
        // Validate vendor exists and is active
        Vendor vendor = vendorRepository.findById(vendorId)
                .orElseThrow(() -> new RuntimeException("Vendor not found with ID: " + vendorId));
        
        if (vendor.getCompliance() == null || !vendor.getCompliance()) {
            throw new RuntimeException("Cannot create PO for inactive vendor: " + vendor.getName());
        }
        
        PurchaseOrder po = new PurchaseOrder();
        po.setTitle(title);
        po.setVendorId(vendorId);
        po.setStatus(POStatus.OPEN);
        po.setSubtotal(BigDecimal.ZERO);
        po.setTax(BigDecimal.ZERO);
        po.setDiscount(BigDecimal.ZERO);
        po.setTotalAmount(BigDecimal.ZERO);
        PurchaseOrder savedPo = purchaseOrderRepository.save(po);
        purchaseOrderService.logHistory(savedPo.getId(), POStatus.OPEN, "PO Created", null, "Purchase Order created for vendor: " + vendor.getName());
        return new PurchaseOrderResponse(savedPo);
    }

    // Get all POs
    @GetMapping
    public Page<PurchaseOrderResponse> getAllPurchaseOrders(Pageable pageable) {
        return purchaseOrderRepository.findAll(pageable)
                .map(PurchaseOrderResponse::new);
    }

    // Add item to PO
    @PostMapping("/{id}/items")
    public ResponseEntity<PurchaseOrderItemResponse> addItem(
            @PathVariable Long id,
            @RequestParam String itemName,
            @RequestParam Integer quantity,
            @RequestParam BigDecimal unitPrice,
            @RequestParam(required = false) BigDecimal tax,
            @RequestParam(required = false) BigDecimal discount) {
        
        PurchaseOrder po = purchaseOrderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Purchase Order not found"));
        
        PurchaseOrderItem item = new PurchaseOrderItem();
        item.setItemName(itemName);
        item.setQuantity(quantity);
        item.setUnitPrice(unitPrice);
        item.setTax(tax != null ? tax : BigDecimal.ZERO);
        item.setDiscount(discount != null ? discount : BigDecimal.ZERO);
        
        PurchaseOrderItem savedItem = purchaseOrderService.addItemToPurchaseOrder(po, item);
        return ResponseEntity.ok(new PurchaseOrderItemResponse(savedItem));
    }

    // Update item quantity/price
    @PutMapping("/items/{itemId}")
    public ResponseEntity<PurchaseOrderItemResponse> updateItem(
            @PathVariable Long itemId,
            @RequestParam(required = false) Integer quantity,
            @RequestParam(required = false) BigDecimal unitPrice,
            @RequestParam(required = false) BigDecimal tax,
            @RequestParam(required = false) BigDecimal discount) {
        
        PurchaseOrderItem item = purchaseOrderService.getAllItems().stream()
                .filter(i -> i.getId().equals(itemId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Item not found"));
        
        if (quantity != null) item.setQuantity(quantity);
        if (unitPrice != null) item.setUnitPrice(unitPrice);
        if (tax != null) item.setTax(tax);
        if (discount != null) item.setDiscount(discount);
        
        PurchaseOrderItem updatedItem = purchaseOrderService.updateItem(
                itemId, 
                item.getQuantity(), 
                item.getUnitPrice(),
                item.getTax(),
                item.getDiscount()
        );
        
        return ResponseEntity.ok(new PurchaseOrderItemResponse(updatedItem));
    }

    // Update delivery status
    @PutMapping("/items/{itemId}/status")
    public ResponseEntity<PurchaseOrderItemResponse> updateItemStatus(
            @PathVariable Long itemId,
            @RequestParam ItemStatus status) {
        
        PurchaseOrderItem updatedItem = purchaseOrderService.updateDeliveryStatus(itemId, status);
        return ResponseEntity.ok(new PurchaseOrderItemResponse(updatedItem));
    }

    // Delete PO
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePurchaseOrder(@PathVariable Long id) {
        purchaseOrderService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/history")
    public ResponseEntity<List<POHistory>> getPOHistory(@PathVariable Long id) {
        return ResponseEntity.ok(purchaseOrderService.getHistory(id));
    }
}