package com.example.spvms.service;

import com.example.spvms.dto.VendorRequest;
import com.example.spvms.model.Vendor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface VendorService {

    Vendor createVendor(VendorRequest request);

    Vendor updateVendor(Long id, VendorRequest request);

    Vendor getVendorById(Long id);

    List<Vendor> getAllVendors();

    void deleteVendor(Long id);

    Page<Vendor> searchVendors(
            Double rating,
            String location,
            String category,
            Boolean compliance,
            Pageable pageable
    );
}
