package com.slpolice.trafficfines.fine.repository;

import com.slpolice.trafficfines.fine.entity.Fine;
import com.slpolice.trafficfines.fine.entity.FineStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * JPA Repository for the Fine entity.
 * Provides data access methods required by the Fine endpoints defined in the implementation plan.
 */
@Repository
public interface FineRepository extends JpaRepository<Fine, Long> {

    /**
     * Find a fine by its unique reference number.
     * Used by GET /api/fines/{referenceNumber} (Public lookup endpoint).
     */
    Optional<Fine> findByReferenceNumber(String referenceNumber);

    /**
     * Find a fine by reference number AND category code.
     * Used by GET /api/fines/verify (Public verification endpoint — driver must provide both).
     */
    Optional<Fine> findByReferenceNumberAndCategoryCategoryCode(String referenceNumber, String categoryCode);

    /**
     * List all fines issued by a specific officer.
     * Used by GET /api/fines/officer/{officerId} (Officer role endpoint).
     */
    List<Fine> findByOfficerId(Long officerId);

    /**
     * List fines by officer and status (useful for reporting).
     */
    List<Fine> findByOfficerIdAndStatus(Long officerId, FineStatus status);

    /**
     * Check whether a fine with the given reference number already exists.
     * Used in reference number uniqueness validation.
     */
    boolean existsByReferenceNumber(String referenceNumber);

    // ── Admin reporting queries ─────────────────────────────────────────────

    long countByStatus(FineStatus status);

    long countByDistrict(String district);

    @Query("SELECT COUNT(f) FROM Fine f WHERE f.status = 'PENDING' AND f.createdAt >= :since")
    long countPendingSince(java.time.LocalDateTime since);

    @Query("SELECT f.district, COUNT(f) FROM Fine f GROUP BY f.district")
    List<Object[]> countByDistrictGrouped();

    @Query("SELECT f.district, COUNT(f) FROM Fine f WHERE f.status = 'PAID' GROUP BY f.district")
    List<Object[]> countPaidByDistrictGrouped();
}
