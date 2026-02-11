package com.example.spvms.specifications;

import com.example.spvms.model.Vendor;
import org.springframework.data.jpa.domain.Specification;

public class VendorSpecification {

    public static Specification<Vendor> filterVendors(
            Double rating,
            String location,
            String category,
            Boolean compliance) {

        return (root, query, criteriaBuilder) -> {

            var predicate = criteriaBuilder.conjunction();

            if (rating != null) {
                predicate = criteriaBuilder.and(
                        predicate,
                        criteriaBuilder.greaterThanOrEqualTo(
                                root.get("rating"), rating)
                );
            }

            if (location != null && !location.isEmpty()) {
                predicate = criteriaBuilder.and(
                        predicate,
                        criteriaBuilder.like(
                                criteriaBuilder.lower(root.get("location")),
                                "%" + location.toLowerCase() + "%")
                );
            }

            if (category != null && !category.isEmpty()) {
                predicate = criteriaBuilder.and(
                        predicate,
                        criteriaBuilder.equal(
                                root.get("category"), category)
                );
            }

            if (compliance != null) {
                predicate = criteriaBuilder.and(
                        predicate,
                        criteriaBuilder.equal(
                                root.get("compliance"), compliance)
                );
            }

            return predicate;
        };
    }
}
