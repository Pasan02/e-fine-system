package com.slpolice.trafficfines.fine.repository;

import com.slpolice.trafficfines.fine.entity.FineCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * JPA Repository for the FineCategory entity.
 */
@Repository
public interface FineCategoryRepository extends JpaRepository<FineCategory, Long> {

    /**
     * Find an active fine category by its unique category code.
     * Used during fine creation and payment verification.
     */
    Optional<FineCategory> findByCategoryCodeAndIsActiveTrue(String categoryCode);

    /**
     * Check if a category code already exists (for admin management).
     */
    boolean existsByCategoryCode(String categoryCode);
}
