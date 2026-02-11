package com.example.spvms.config;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import com.example.spvms.enums.RequisitionStatus;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import com.example.spvms.model.Role;
import com.example.spvms.model.*;
import com.example.spvms.repository.*;

@Component
public class DataSeedRunner implements ApplicationRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final VendorRepository vendorRepository;
    private final PurchaseRequisitionRepository purchaseRequisitionRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public DataSeedRunner(RoleRepository roleRepository, UserRepository userRepository, VendorRepository vendorRepository, PurchaseRequisitionRepository purchaseRequisitionRepository) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.vendorRepository = vendorRepository;
        this.purchaseRequisitionRepository = purchaseRequisitionRepository;
    }

    @Override
    public void run(ApplicationArguments args) {
        // create roles if absent
        createRoleIfNotExists("ADMIN", "Administrator");
        createRoleIfNotExists("PROCUREMENT", "Procurement team");
        createRoleIfNotExists("FINANCE", "Finance team");
        createRoleIfNotExists("VENDOR", "Vendor");

        // seed admin user if not exists
        if (!userRepository.existsByEmail("admin@company.com")) {
            User admin = new User();
            admin.setName("System Administrator");
            admin.setEmail("admin@company.com");
            admin.setPassword(passwordEncoder.encode("Admin@123")); // change this password
            admin.setIsActive(true);

            Set<Role> roles = new HashSet<>();
            Role adminRole = roleRepository.findByName("ADMIN").orElseThrow();
            roles.add(adminRole);
            admin.setRoles(roles);

            userRepository.save(admin);

            System.out.println("Seeded default admin user: admin@company.com / Admin@123");
        }

        // seed sample vendors if none exist
        if (vendorRepository.count() == 0) {
            createVendor("Tech Solutions Inc", "tech@solutions.com", "+1-555-0101", "New York", "IT Services", true, 4.5);
            createVendor("Office Supplies Co", "contact@officesupplies.com", "+1-555-0102", "Chicago", "Office Supplies", true, 4.2);
            createVendor("Electronics Hub", "sales@electronichub.com", "+1-555-0103", "San Francisco", "Electronics", true, 4.8);
            createVendor("Construction Materials Ltd", "info@constructionmat.com", "+1-555-0104", "Houston", "Construction", false, 3.9);
            createVendor("Global IT Services", "contact@globalit.com", "+1-555-0105", "Seattle", "IT Services", true, 4.6);
            createVendor("Premium Office Gear", "sales@premiumoffice.com", "+1-555-0106", "Boston", "Office Supplies", true, 4.3);
            System.out.println("Seeded sample vendors");
        }

        // seed sample purchase requisitions - always recreate for testing
        try {
            purchaseRequisitionRepository.deleteAll();
            createPurchaseRequisition("REQ001", 1L, new BigDecimal("50000"), 10, "Office furniture and equipment");
            createPurchaseRequisition("REQ002", 1L, new BigDecimal("75000"), 5, "IT hardware procurement");
            createPurchaseRequisition("REQ003", 1L, new BigDecimal("30000"), 20, "Stationery supplies");
            System.out.println("Seeded sample purchase requisitions");
        } catch (Exception e) {
            System.err.println("Error seeding purchase requisitions: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void createRoleIfNotExists(String name, String desc) {
        if (!roleRepository.existsByName(name)) {
            Role r = new Role();
            r.setName(name);
            r.setDescription(desc);
            roleRepository.save(r);
        }
    }

    private void createVendor(String name, String email, String phone, String location, String category, boolean compliance, double rating) {
        Vendor vendor = new Vendor();
        vendor.setName(name);
        vendor.setEmail(email);
        vendor.setPhone(phone);
        vendor.setLocation(location);
        vendor.setCategory(category);
        vendor.setCompliance(compliance);
        vendor.setRating(rating);
        vendor.setCreatedAt(LocalDateTime.now());
        vendor.setUpdatedAt(LocalDateTime.now());
        vendorRepository.save(vendor);
    }

    private void createPurchaseRequisition(String reqNumber, Long requesterId, BigDecimal totalAmount, Integer quantity, String description) {
        try {
            PurchaseRequisition pr = new PurchaseRequisition();
            pr.setRequisitionNumber(reqNumber);
            pr.setRequesterId(requesterId);
            pr.setTotalAmount(totalAmount);
            pr.setQuantity(quantity);
            pr.setDescription(description);
            pr.setStatus(RequisitionStatus.SUBMITTED);
            pr.setRequisitionDate(LocalDate.now());
            PurchaseRequisition saved = purchaseRequisitionRepository.save(pr);
            System.out.println("Created PR: " + saved.getPrNumber());
        } catch (Exception e) {
            System.err.println("Failed to create PR " + reqNumber + ": " + e.getMessage());
        }
    }
}