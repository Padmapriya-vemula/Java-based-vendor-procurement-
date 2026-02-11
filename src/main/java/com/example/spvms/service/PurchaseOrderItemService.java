package com.example.spvms.service;

import com.example.spvms.enums.ItemStatus;
import com.example.spvms.enums.POStatus;
import com.example.spvms.model.PurchaseOrder;
import com.example.spvms.model.PurchaseOrderItem;
import com.example.spvms.repository.PurchaseOrderItemRepository;
import com.example.spvms.repository.PurchaseOrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.List;

@Service
@Transactional
public class PurchaseOrderItemService {

    @Autowired
    private PurchaseOrderItemRepository itemRepository;

    @Autowired
    private PurchaseOrderRepository purchaseOrderRepository;

    public PurchaseOrderItem addItem(Long poId, String itemName, Integer quantity, 
                                   BigDecimal unitPrice, BigDecimal tax, BigDecimal discount) {
        PurchaseOrder po = purchaseOrderRepository.findById(poId)
                .orElseThrow(() -> new RuntimeException("Purchase Order not found"));

        PurchaseOrderItem item = new PurchaseOrderItem();
        item.setItemName(itemName);
        item.setQuantity(quantity);
        item.setUnitPrice(unitPrice != null ? unitPrice : BigDecimal.ZERO);
        item.setTax(tax != null ? tax : BigDecimal.ZERO);
        item.setDiscount(discount != null ? discount : BigDecimal.ZERO);
        item.setPurchaseOrder(po);
        
        calculateItemTotals(item);
        itemRepository.save(item);
        recalculatePOTotals(po);
        
        return item;
    }

    public PurchaseOrderItem updateItem(Long itemId, Integer quantity, BigDecimal unitPrice, 
                                      BigDecimal tax, BigDecimal discount) {
        PurchaseOrderItem item = itemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Item not found"));

        item.setQuantity(quantity);
        item.setUnitPrice(unitPrice);
        item.setTax(tax);
        item.setDiscount(discount);
        
        calculateItemTotals(item);
        itemRepository.save(item);
        recalculatePOTotals(item.getPurchaseOrder());
        
        return item;
    }

    public PurchaseOrderItem updateItemStatus(Long itemId, ItemStatus status) {
        PurchaseOrderItem item = itemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Item not found"));

        item.setStatus(status);
        itemRepository.save(item);
        recalculatePOTotals(item.getPurchaseOrder());
        
        return item;
    }

    public PurchaseOrderItem deliverItem(Long itemId) {
        PurchaseOrderItem item = itemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Item not found"));

        item.setStatus(ItemStatus.DELIVERED);
        itemRepository.save(item);
        recalculatePOTotals(item.getPurchaseOrder());
        
        return item;
    }

    public PurchaseOrder closePurchaseOrder(Long poId) {
        PurchaseOrder po = purchaseOrderRepository.findById(poId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Purchase Order not found"));

        List<PurchaseOrderItem> items = itemRepository.findByPurchaseOrderId(poId);
        boolean allDelivered = items.stream()
                .allMatch(item -> item.getStatus() == ItemStatus.DELIVERED);

        if (!allDelivered) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot close PO: Not all items are delivered");
        }

        po.setStatus(POStatus.CLOSED);
        return purchaseOrderRepository.save(po);
    }

    private void calculateItemTotals(PurchaseOrderItem item) {
        BigDecimal unitPrice = item.getUnitPrice() != null ? item.getUnitPrice() : BigDecimal.ZERO;
        BigDecimal tax = item.getTax() != null ? item.getTax() : BigDecimal.ZERO;
        BigDecimal discount = item.getDiscount() != null ? item.getDiscount() : BigDecimal.ZERO;
        
        BigDecimal itemSubtotal = unitPrice.multiply(new BigDecimal(item.getQuantity()));
        BigDecimal itemTotal = itemSubtotal.add(tax).subtract(discount);
        
        item.setItemSubtotal(itemSubtotal);
        item.setItemTotal(itemTotal);
    }

    private void recalculatePOTotals(PurchaseOrder po) {
        List<PurchaseOrderItem> items = itemRepository.findByPurchaseOrderId(po.getId());
        
        BigDecimal poSubtotal = items.stream()
                .map(item -> item.getItemSubtotal() != null ? item.getItemSubtotal() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        BigDecimal poTax = items.stream()
                .map(item -> item.getTax() != null ? item.getTax() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        BigDecimal poDiscount = items.stream()
                .map(item -> item.getDiscount() != null ? item.getDiscount() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        BigDecimal totalCost = poSubtotal.add(poTax).subtract(poDiscount);
        
        po.setSubtotal(poSubtotal);
        po.setTax(poTax);
        po.setDiscount(poDiscount);
        po.setTotalAmount(totalCost);
        
        purchaseOrderRepository.save(po);
    }
}