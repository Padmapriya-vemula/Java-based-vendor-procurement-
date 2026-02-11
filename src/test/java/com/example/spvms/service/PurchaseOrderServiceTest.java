package com.example.spvms.service;

import com.example.spvms.enums.ItemStatus;
import com.example.spvms.enums.POStatus;
import com.example.spvms.model.PurchaseOrder;
import com.example.spvms.model.PurchaseOrderItem;
import com.example.spvms.repository.PurchaseOrderItemRepository;
import com.example.spvms.repository.PurchaseOrderRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PurchaseOrderServiceTest {

    @Mock
    private PurchaseOrderRepository purchaseOrderRepository;

    @Mock
    private PurchaseOrderItemRepository itemRepository;

    @InjectMocks
    private PurchaseOrderService purchaseOrderService;

    @Test
    void addItemToPurchaseOrder_Success() {
        PurchaseOrder po = new PurchaseOrder();
        po.setId(1L);
        po.setItems(new ArrayList<>());

        PurchaseOrderItem item = new PurchaseOrderItem();
        item.setItemName("Test Item");
        item.setQuantity(2);
        item.setUnitPrice(BigDecimal.valueOf(100));
        item.setTax(BigDecimal.valueOf(10));
        item.setDiscount(BigDecimal.valueOf(5));

        when(itemRepository.save(any(PurchaseOrderItem.class))).thenReturn(item);
        when(purchaseOrderRepository.save(any(PurchaseOrder.class))).thenReturn(po);

        PurchaseOrderItem result = purchaseOrderService.addItemToPurchaseOrder(po, item);

        assertNotNull(result);
        assertEquals(BigDecimal.valueOf(200), item.getItemSubtotal());
        verify(itemRepository, times(1)).save(any(PurchaseOrderItem.class));
        verify(purchaseOrderRepository, times(1)).save(any(PurchaseOrder.class));
    }

    @Test
    void updateDeliveryStatus_AllDelivered_ClosePO() {
        Long itemId = 1L;
        PurchaseOrderItem item = new PurchaseOrderItem();
        item.setId(itemId);
        item.setStatus(ItemStatus.PENDING);

        PurchaseOrder po = new PurchaseOrder();
        po.setId(1L);
        po.setStatus(POStatus.OPEN);
        po.setItems(new ArrayList<>());
        po.getItems().add(item);
        item.setPurchaseOrder(po);

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(itemRepository.save(any(PurchaseOrderItem.class))).thenReturn(item);
        when(purchaseOrderRepository.save(any(PurchaseOrder.class))).thenReturn(po);

        PurchaseOrderItem result = purchaseOrderService.updateDeliveryStatus(itemId, ItemStatus.DELIVERED);

        assertNotNull(result);
        assertEquals(ItemStatus.DELIVERED, result.getStatus());
        assertEquals(POStatus.CLOSED, po.getStatus());
        verify(purchaseOrderRepository, times(1)).save(po);
    }

    @Test
    void updateDeliveryStatus_NotAllDelivered_KeepOpen() {
        Long itemId = 1L;
        PurchaseOrderItem item1 = new PurchaseOrderItem();
        item1.setId(itemId);
        item1.setStatus(ItemStatus.PENDING);

        PurchaseOrderItem item2 = new PurchaseOrderItem();
        item2.setId(2L);
        item2.setStatus(ItemStatus.PENDING);

        PurchaseOrder po = new PurchaseOrder();
        po.setId(1L);
        po.setStatus(POStatus.OPEN);
        po.setItems(new ArrayList<>());
        po.getItems().add(item1);
        po.getItems().add(item2);
        item1.setPurchaseOrder(po);
        item2.setPurchaseOrder(po);

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item1));
        when(itemRepository.save(any(PurchaseOrderItem.class))).thenReturn(item1);

        PurchaseOrderItem result = purchaseOrderService.updateDeliveryStatus(itemId, ItemStatus.DELIVERED);

        assertNotNull(result);
        assertEquals(ItemStatus.DELIVERED, result.getStatus());
        assertEquals(POStatus.OPEN, po.getStatus());
        verify(purchaseOrderRepository, never()).save(po);
    }

    @Test
    void delete_Success() {
        Long poId = 1L;

        when(purchaseOrderRepository.existsById(poId)).thenReturn(true);
        doNothing().when(purchaseOrderRepository).deleteById(poId);

        purchaseOrderService.delete(poId);

        verify(purchaseOrderRepository, times(1)).existsById(poId);
        verify(purchaseOrderRepository, times(1)).deleteById(poId);
    }

    @Test
    void delete_NotFound() {
        Long poId = 999L;

        when(purchaseOrderRepository.existsById(poId)).thenReturn(false);

        assertThrows(RuntimeException.class, () -> purchaseOrderService.delete(poId));
        verify(purchaseOrderRepository, times(1)).existsById(poId);
        verify(purchaseOrderRepository, never()).deleteById(poId);
    }
}
