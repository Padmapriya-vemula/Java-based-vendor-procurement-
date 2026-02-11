package com.example.spvms.repository;

import com.example.spvms.model.Vendor;
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

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@Disabled("Requires Docker - run manually with Docker running")
@DataJpaTest
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class VendorRepositoryTest {

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
    private VendorRepository vendorRepository;

    @Test
    void saveAndFindVendor() {
        Vendor vendor = new Vendor();
        vendor.setName("Test Vendor");
        vendor.setEmail("test@vendor.com");
        vendor.setPhone("1234567890");
        vendor.setRating(4.5);

        Vendor saved = vendorRepository.save(vendor);

        assertNotNull(saved.getId());
        assertEquals("Test Vendor", saved.getName());
    }

    @Test
    void findById_Found() {
        Vendor vendor = new Vendor();
        vendor.setName("Find Test");
        vendor.setEmail("find@test.com");
        Vendor saved = vendorRepository.save(vendor);

        Optional<Vendor> found = vendorRepository.findById(saved.getId());

        assertTrue(found.isPresent());
        assertEquals("Find Test", found.get().getName());
    }

    @Test
    void findById_NotFound() {
        Optional<Vendor> found = vendorRepository.findById(999L);

        assertFalse(found.isPresent());
    }

    @Test
    void findAll() {
        Vendor vendor1 = new Vendor();
        vendor1.setName("Vendor 1");
        vendor1.setEmail("v1@test.com");

        Vendor vendor2 = new Vendor();
        vendor2.setName("Vendor 2");
        vendor2.setEmail("v2@test.com");

        vendorRepository.save(vendor1);
        vendorRepository.save(vendor2);

        List<Vendor> vendors = vendorRepository.findAll();

        assertTrue(vendors.size() >= 2);
    }

    @Test
    void deleteVendor() {
        Vendor vendor = new Vendor();
        vendor.setName("To Delete");
        vendor.setEmail("delete@test.com");
        Vendor saved = vendorRepository.save(vendor);

        vendorRepository.deleteById(saved.getId());

        Optional<Vendor> found = vendorRepository.findById(saved.getId());
        assertFalse(found.isPresent());
    }

    @Test
    void updateVendor() {
        Vendor vendor = new Vendor();
        vendor.setName("Original");
        vendor.setEmail("original@test.com");
        Vendor saved = vendorRepository.save(vendor);

        saved.setName("Updated");
        saved.setEmail("updated@test.com");
        Vendor updated = vendorRepository.save(saved);

        assertEquals("Updated", updated.getName());
        assertEquals("updated@test.com", updated.getEmail());
    }
}
