package com.example.spvms.service;

import com.example.spvms.dto.VendorRequest;
import com.example.spvms.model.Vendor;
import com.example.spvms.repository.VendorRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VendorServiceImplTest {

    @Mock
    private VendorRepository vendorRepository;

    @InjectMocks
    private VendorServiceImpl vendorService;

    @Test
    void createVendor_Success() {
        VendorRequest request = new VendorRequest();
        request.setName("Test Vendor");
        request.setEmail("test@vendor.com");
        request.setPhone("1234567890");
        request.setRating(4.5);

        Vendor savedVendor = new Vendor();
        savedVendor.setId(1L);
        savedVendor.setName("Test Vendor");

        when(vendorRepository.save(any(Vendor.class))).thenReturn(savedVendor);

        Vendor result = vendorService.createVendor(request);

        assertNotNull(result);
        assertEquals("Test Vendor", result.getName());
        verify(vendorRepository, times(1)).save(any(Vendor.class));
    }

    @Test
    void updateVendor_Success() {
        Long vendorId = 1L;
        VendorRequest request = new VendorRequest();
        request.setName("Updated Vendor");
        request.setEmail("updated@vendor.com");

        Vendor existingVendor = new Vendor();
        existingVendor.setId(vendorId);
        existingVendor.setName("Old Name");

        when(vendorRepository.findById(vendorId)).thenReturn(Optional.of(existingVendor));
        when(vendorRepository.save(any(Vendor.class))).thenReturn(existingVendor);

        Vendor result = vendorService.updateVendor(vendorId, request);

        assertNotNull(result);
        assertEquals("Updated Vendor", result.getName());
        verify(vendorRepository, times(1)).findById(vendorId);
        verify(vendorRepository, times(1)).save(any(Vendor.class));
    }

    @Test
    void updateVendor_NotFound() {
        Long vendorId = 999L;
        VendorRequest request = new VendorRequest();

        when(vendorRepository.findById(vendorId)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> vendorService.updateVendor(vendorId, request));
        verify(vendorRepository, times(1)).findById(vendorId);
        verify(vendorRepository, never()).save(any(Vendor.class));
    }

    @Test
    void getVendorById_Found() {
        Long vendorId = 1L;
        Vendor vendor = new Vendor();
        vendor.setId(vendorId);
        vendor.setName("Test Vendor");

        when(vendorRepository.findById(vendorId)).thenReturn(Optional.of(vendor));

        Vendor result = vendorService.getVendorById(vendorId);

        assertNotNull(result);
        assertEquals(vendorId, result.getId());
        verify(vendorRepository, times(1)).findById(vendorId);
    }

    @Test
    void getVendorById_NotFound() {
        Long vendorId = 999L;

        when(vendorRepository.findById(vendorId)).thenReturn(Optional.empty());

        Vendor result = vendorService.getVendorById(vendorId);

        assertNull(result);
        verify(vendorRepository, times(1)).findById(vendorId);
    }

    @Test
    void deleteVendor_Success() {
        Long vendorId = 1L;

        doNothing().when(vendorRepository).deleteById(vendorId);

        vendorService.deleteVendor(vendorId);

        verify(vendorRepository, times(1)).deleteById(vendorId);
    }
}
