package com.example.spvms.repository;

import com.example.spvms.model.Vendor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface VendorRepository
        extends JpaRepository<Vendor, Long>,
                JpaSpecificationExecutor<Vendor> {
}
