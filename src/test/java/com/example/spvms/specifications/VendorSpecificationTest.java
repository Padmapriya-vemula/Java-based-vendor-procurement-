package com.example.spvms.specifications;

import com.example.spvms.model.Vendor;
import jakarta.persistence.criteria.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VendorSpecificationTest {

    @Mock
    private Root<Vendor> root;

    @Mock
    private CriteriaQuery<?> query;

    @Mock
    private CriteriaBuilder criteriaBuilder;

    @Mock
    private Path<Object> path;

    @Mock
    private Expression<String> stringExpression;

    @Mock
    private Predicate predicate;

    @Test
    void filterVendors_AllFiltersNull_ReturnsConjunction() {
        when(criteriaBuilder.conjunction()).thenReturn(predicate);

        Specification<Vendor> spec = VendorSpecification.filterVendors(null, null, null, null);
        Predicate result = spec.toPredicate(root, query, criteriaBuilder);

        assertNotNull(result);
        verify(criteriaBuilder).conjunction();
        verify(criteriaBuilder, never()).greaterThanOrEqualTo(any(), any(Double.class));
    }

    @Test
    void filterVendors_WithRating_AppliesRatingFilter() {
        Double rating = 4.0;

        when(criteriaBuilder.conjunction()).thenReturn(predicate);
        when(root.get("rating")).thenReturn(path);
        when(criteriaBuilder.greaterThanOrEqualTo(any(), eq(rating))).thenReturn(predicate);
        when(criteriaBuilder.and(any(Predicate.class), any(Predicate.class))).thenReturn(predicate);

        Specification<Vendor> spec = VendorSpecification.filterVendors(rating, null, null, null);
        Predicate result = spec.toPredicate(root, query, criteriaBuilder);

        assertNotNull(result);
        verify(root).get("rating");
        verify(criteriaBuilder).greaterThanOrEqualTo(any(), eq(rating));
    }

    @Test
    void filterVendors_WithLocation_AppliesLocationFilter() {
        String location = "New York";

        when(criteriaBuilder.conjunction()).thenReturn(predicate);
        when(root.get("location")).thenReturn(path);
        when(criteriaBuilder.lower(any())).thenReturn(stringExpression);
        when(criteriaBuilder.like(any(), anyString())).thenReturn(predicate);
        when(criteriaBuilder.and(any(Predicate.class), any(Predicate.class))).thenReturn(predicate);

        Specification<Vendor> spec = VendorSpecification.filterVendors(null, location, null, null);
        Predicate result = spec.toPredicate(root, query, criteriaBuilder);

        assertNotNull(result);
        verify(root).get("location");
        verify(criteriaBuilder).like(any(), contains("new york"));
    }

    @Test
    void filterVendors_WithEmptyLocation_SkipsLocationFilter() {
        when(criteriaBuilder.conjunction()).thenReturn(predicate);

        Specification<Vendor> spec = VendorSpecification.filterVendors(null, "", null, null);
        Predicate result = spec.toPredicate(root, query, criteriaBuilder);

        assertNotNull(result);
        verify(root, never()).get("location");
    }

    @Test
    void filterVendors_WithCategory_AppliesCategoryFilter() {
        String category = "Electronics";

        when(criteriaBuilder.conjunction()).thenReturn(predicate);
        when(root.get("category")).thenReturn(path);
        when(criteriaBuilder.equal(any(), eq(category))).thenReturn(predicate);
        when(criteriaBuilder.and(any(Predicate.class), any(Predicate.class))).thenReturn(predicate);

        Specification<Vendor> spec = VendorSpecification.filterVendors(null, null, category, null);
        Predicate result = spec.toPredicate(root, query, criteriaBuilder);

        assertNotNull(result);
        verify(root).get("category");
        verify(criteriaBuilder).equal(any(), eq(category));
    }

    @Test
    void filterVendors_WithEmptyCategory_SkipsCategoryFilter() {
        when(criteriaBuilder.conjunction()).thenReturn(predicate);

        Specification<Vendor> spec = VendorSpecification.filterVendors(null, null, "", null);
        Predicate result = spec.toPredicate(root, query, criteriaBuilder);

        assertNotNull(result);
        verify(root, never()).get("category");
    }

    @Test
    void filterVendors_WithCompliance_AppliesComplianceFilter() {
        Boolean compliance = true;

        when(criteriaBuilder.conjunction()).thenReturn(predicate);
        when(root.get("compliance")).thenReturn(path);
        when(criteriaBuilder.equal(any(), eq(compliance))).thenReturn(predicate);
        when(criteriaBuilder.and(any(Predicate.class), any(Predicate.class))).thenReturn(predicate);

        Specification<Vendor> spec = VendorSpecification.filterVendors(null, null, null, compliance);
        Predicate result = spec.toPredicate(root, query, criteriaBuilder);

        assertNotNull(result);
        verify(root).get("compliance");
        verify(criteriaBuilder).equal(any(), eq(compliance));
    }

    @Test
    void filterVendors_AllFilters_AppliesAllFilters() {
        Double rating = 4.5;
        String location = "Boston";
        String category = "IT";
        Boolean compliance = true;

        when(criteriaBuilder.conjunction()).thenReturn(predicate);
        when(root.get(anyString())).thenReturn(path);
        when(criteriaBuilder.lower(any())).thenReturn(stringExpression);
        when(criteriaBuilder.greaterThanOrEqualTo(any(), any(Double.class))).thenReturn(predicate);
        when(criteriaBuilder.like(any(), anyString())).thenReturn(predicate);
        when(criteriaBuilder.equal(any(), anyString())).thenReturn(predicate);
        when(criteriaBuilder.equal(any(), any(Boolean.class))).thenReturn(predicate);
        when(criteriaBuilder.and(any(Predicate.class), any(Predicate.class))).thenReturn(predicate);

        Specification<Vendor> spec = VendorSpecification.filterVendors(rating, location, category, compliance);
        Predicate result = spec.toPredicate(root, query, criteriaBuilder);

        assertNotNull(result);
        verify(root).get("rating");
        verify(root).get("location");
        verify(root).get("category");
        verify(root).get("compliance");
    }
}
