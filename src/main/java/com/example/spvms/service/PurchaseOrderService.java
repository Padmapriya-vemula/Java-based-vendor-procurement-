package com.example.spvms.service;

import com.example.spvms.enums.ItemStatus;
import com.example.spvms.enums.POStatus;
import com.example.spvms.model.POHistory;
import com.example.spvms.model.PurchaseOrder;
import com.example.spvms.model.PurchaseOrderItem;
import com.example.spvms.repository.POHistoryRepository;
import com.example.spvms.repository.PurchaseOrderItemRepository;
import com.example.spvms.repository.PurchaseOrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class PurchaseOrderService {

    @Autowired
    private PurchaseOrderRepository purchaseOrderRepository;

    @Autowired
    private PurchaseOrderItemRepository itemRepository;

    @Autowired
    private POHistoryRepository historyRepository;

    public PurchaseOrderItem addItemToPurchaseOrder(
            PurchaseOrder purchaseOrder,
            PurchaseOrderItem item) {

        // Calculate item totals
        calculateItemTotals(item);
        item.setPurchaseOrder(purchaseOrder);
        PurchaseOrderItem savedItem = itemRepository.save(item);

        recalculateTotals(purchaseOrder);
        purchaseOrderRepository.save(purchaseOrder);

        return savedItem;
    }

    public PurchaseOrderItem updateItem(
            Long itemId,
            Integer quantity,
            BigDecimal unitPrice,
            BigDecimal tax,
            BigDecimal discount) {

        PurchaseOrderItem item = itemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Item not found"));

        item.setQuantity(quantity);
        item.setUnitPrice(unitPrice);
        item.setTax(tax != null ? tax : BigDecimal.ZERO);
        item.setDiscount(discount != null ? discount : BigDecimal.ZERO);
        
        calculateItemTotals(item);
        PurchaseOrderItem updatedItem = itemRepository.save(item);

        PurchaseOrder po = item.getPurchaseOrder();
        recalculateTotals(po);
        purchaseOrderRepository.save(po);

        return updatedItem;
    }

    private void calculateItemTotals(PurchaseOrderItem item) {
        BigDecimal subtotal = item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
        BigDecimal tax = item.getTax() != null ? item.getTax() : BigDecimal.ZERO;
        BigDecimal discount = item.getDiscount() != null ? item.getDiscount() : BigDecimal.ZERO;
        
        item.setItemSubtotal(subtotal);
        BigDecimal total = subtotal.add(tax).subtract(discount);
        item.setItemTotal(total);
    }

    public void recalculateTotals(PurchaseOrder po) {
        BigDecimal subtotal = po.getItems().stream()
                .map(item -> item.getItemSubtotal() != null ? item.getItemSubtotal() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalTax = po.getItems().stream()
                .map(item -> item.getTax() != null ? item.getTax() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalDiscount = po.getItems().stream()
                .map(item -> item.getDiscount() != null ? item.getDiscount() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal total = subtotal.add(totalTax).subtract(totalDiscount);

        po.setSubtotal(subtotal);
        po.setTax(totalTax);
        po.setDiscount(totalDiscount);
        po.setTotalAmount(total);
    }

    public PurchaseOrderItem updateDeliveryStatus(
            Long itemId,
            ItemStatus status) {

        PurchaseOrderItem item = itemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Item not found"));

        item.setStatus(status);
        PurchaseOrderItem updatedItem = itemRepository.save(item);

        PurchaseOrder po = item.getPurchaseOrder();

        boolean allDelivered = po.getItems().stream()
                .allMatch(i -> i.getStatus() == ItemStatus.DELIVERED);

        if (allDelivered) {
            po.setStatus(POStatus.CLOSED);
            purchaseOrderRepository.save(po);
            logHistory(po.getId(), POStatus.CLOSED, "Status Changed", null, "All items delivered, PO closed");
        }

        return updatedItem;
    }

    public List<PurchaseOrderItem> getAllItems() {
        return itemRepository.findAll();
    }

    public void delete(Long id) {
        if (!purchaseOrderRepository.existsById(id)) {
            throw new RuntimeException("Purchase Order not found with id: " + id);
        }
        purchaseOrderRepository.deleteById(id);
    }

    public List<POHistory> getHistory(Long poId) {
        return historyRepository.findByPoIdOrderByCreatedAtDesc(poId);
    }

    public void logHistory(Long poId, POStatus status, String action, Long changedBy, String remarks) {
        POHistory history = new POHistory();
        history.setPoId(poId);
        history.setStatus(status);
        history.setAction(action);
        history.setChangedBy(changedBy);
        history.setRemarks(remarks);
        historyRepository.save(history);
    }
}
