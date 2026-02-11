package com.example.spvms.repository;

import com.example.spvms.enums.POStatus;
import com.example.spvms.model.PurchaseOrder;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@Disabled("Requires Docker - run manually with Docker running")
@DataJpaTest
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class PurchaseOrderRepositoryTest {

    @Container
    static MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8.0")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", mysql::getJdbcUrl);
        registry.add("spring.datasource.username", mysql::getUsername);
        registry.add("spring.datasource.password", mysql::getPassword);
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
    }

    @Autowired
    private PurchaseOrderRepository purchaseOrderRepository;

    @Test
    void savePurchaseOrder() {
        PurchaseOrder po = new PurchaseOrder();
        po.setTitle("Test PO");
        po.setStatus(POStatus.OPEN);
        po.setTotalAmount(BigDecimal.valueOf(1000));

        PurchaseOrder saved = purchaseOrderRepository.save(po);

        assertNotNull(saved.getId());
        assertEquals("Test PO", saved.getTitle());
        assertEquals(POStatus.OPEN, saved.getStatus());
    }

    @Test
    void findById() {
        PurchaseOrder po = new PurchaseOrder();
        po.setTitle("Find Test");
        po.setStatus(POStatus.OPEN);
        PurchaseOrder saved = purchaseOrderRepository.save(po);

        Optional<PurchaseOrder> found = purchaseOrderRepository.findById(saved.getId());

        assertTrue(found.isPresent());
        assertEquals("Find Test", found.get().getTitle());
    }

    @Test
    void updateStatus() {
        PurchaseOrder po = new PurchaseOrder();
        po.setTitle("Status Test");
        po.setStatus(POStatus.OPEN);
        PurchaseOrder saved = purchaseOrderRepository.save(po);

        saved.setStatus(POStatus.CLOSED);
        PurchaseOrder updated = purchaseOrderRepository.save(saved);

        assertEquals(POStatus.CLOSED, updated.getStatus());
    }

    @Test
    void deletePurchaseOrder() {
        PurchaseOrder po = new PurchaseOrder();
        po.setTitle("Delete Test");
        po.setStatus(POStatus.OPEN);
        PurchaseOrder saved = purchaseOrderRepository.save(po);

        purchaseOrderRepository.deleteById(saved.getId());

        Optional<PurchaseOrder> found = purchaseOrderRepository.findById(saved.getId());
        assertFalse(found.isPresent());
    }
}
