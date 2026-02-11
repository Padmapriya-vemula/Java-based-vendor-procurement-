package com.example.spvms.service;

import com.example.spvms.dto.VendorRequest;
import com.example.spvms.model.Vendor;
import com.example.spvms.repository.VendorRepository;
import com.example.spvms.specifications.VendorSpecification;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class VendorServiceImpl implements VendorService {

    @Autowired
    private VendorRepository vendorRepository;

    @Override
    public Vendor createVendor(VendorRequest request) {

        Vendor vendor = new Vendor();
        vendor.setName(request.getName());
        vendor.setUserName(request.getUserName());
        vendor.setEmail(request.getEmail());
        vendor.setPhone(request.getPhone());
        vendor.setAddress(request.getAddress());

        // Sprint-3 fields
        vendor.setRating(request.getRating());
        vendor.setLocation(request.getLocation());
        vendor.setCategory(request.getCategory());
        vendor.setCompliance(request.getCompliance());

        vendor.setCreatedAt(LocalDateTime.now());
        vendor.setUpdatedAt(LocalDateTime.now());

        return vendorRepository.save(vendor);
    }

    @Override
    public Vendor updateVendor(Long id, VendorRequest request) {

        Vendor vendor = vendorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Vendor not found"));

        vendor.setName(request.getName());
        vendor.setUserName(request.getUserName());
        vendor.setEmail(request.getEmail());
        vendor.setPhone(request.getPhone());
        vendor.setAddress(request.getAddress());

        vendor.setRating(request.getRating());
        vendor.setLocation(request.getLocation());
        vendor.setCategory(request.getCategory());
        vendor.setCompliance(request.getCompliance());

        vendor.setUpdatedAt(LocalDateTime.now());

        return vendorRepository.save(vendor);
    }

    @Override
    public Vendor getVendorById(Long id) {
        return vendorRepository.findById(id).orElse(null);
    }

    @Override
    public List<Vendor> getAllVendors() {
        return vendorRepository.findAll();
    }

    @Override
    public void deleteVendor(Long id) {
        vendorRepository.deleteById(id);
    }

    @Override
    public Page<Vendor> searchVendors(
            Double rating,
            String location,
            String category,
            Boolean compliance,
            Pageable pageable) {

        Specification<Vendor> spec =
                VendorSpecification.filterVendors(
                        rating, location, category, compliance
                );

        return vendorRepository.findAll(spec, pageable);
    }
}
