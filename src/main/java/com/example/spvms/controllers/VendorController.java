package com.example.spvms.controllers;

import com.example.spvms.dto.ActiveVendorDto;
import com.example.spvms.model.Vendor;
import com.example.spvms.repository.VendorRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/vendors")
public class VendorController {

    private final VendorRepository vendorRepository;

    public VendorController(VendorRepository vendorRepository) {
        this.vendorRepository = vendorRepository;
    }

    @GetMapping("/search")
    public Page<Vendor> searchVendors(
            @RequestParam(required = false) Double rating,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Boolean compliance,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size) {
        
        Specification<Vendor> spec = (root, query, cb) -> {
            var predicates = new java.util.ArrayList<jakarta.persistence.criteria.Predicate>();
            if (rating != null) predicates.add(cb.greaterThanOrEqualTo(root.get("rating"), rating));
            if (location != null && !location.isEmpty()) predicates.add(cb.like(cb.lower(root.get("location")), "%" + location.toLowerCase() + "%"));
            if (category != null && !category.isEmpty()) predicates.add(cb.equal(root.get("category"), category));
            if (compliance != null) predicates.add(cb.equal(root.get("compliance"), compliance));
            return cb.and(predicates.toArray(new jakarta.persistence.criteria.Predicate[0]));
        };
        
        return vendorRepository.findAll(spec, PageRequest.of(page, size, Sort.by("rating").descending()));
    }

    @GetMapping("/active")
    public List<ActiveVendorDto> getActiveVendors() {
        return vendorRepository.findAll().stream()
                .filter(v -> v.getCompliance() != null && v.getCompliance())
                .map(v -> new ActiveVendorDto(
                        v.getId(),
                        v.getName(),
                        v.getRating(),
                        v.getCategory(),
                        v.getLocation()
                ))
                .sorted((a, b) -> Double.compare(b.getRating() != null ? b.getRating() : 0, 
                                                   a.getRating() != null ? a.getRating() : 0))
                .collect(Collectors.toList());
    }

    @PostMapping
    public Vendor createVendor(@RequestParam String name,
                                @RequestParam String email,
                                @RequestParam String phone,
                                @RequestParam String location,
                                @RequestParam String category,
                                @RequestParam Double rating,
                                @RequestParam Boolean compliance) {
        
        if (!phone.matches("^[0-9]{10}$")) {
            throw new RuntimeException("Phone number must be exactly 10 digits");
        }
        
        Vendor vendor = new Vendor();
        vendor.setName(name);
        vendor.setEmail(email);
        vendor.setPhone(phone);
        vendor.setLocation(location);
        vendor.setCategory(category);
        vendor.setRating(rating);
        vendor.setCompliance(compliance);
        vendor.setCreatedAt(LocalDateTime.now());
        vendor.setUpdatedAt(LocalDateTime.now());
        return vendorRepository.save(vendor);
    }

    @GetMapping("/{id}")
    public Vendor getVendor(@PathVariable Long id) {
        return vendorRepository.findById(id).orElse(null);
    }

    @PutMapping("/{id}")
    public Vendor updateVendor(@PathVariable Long id, @RequestBody Vendor vendor) {
        vendor.setId(id);
        vendor.setUpdatedAt(LocalDateTime.now());
        return vendorRepository.save(vendor);
    }

    @DeleteMapping("/{id}")
    public void deleteVendor(@PathVariable Long id) {
        vendorRepository.deleteById(id);
    }
}