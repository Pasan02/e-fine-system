package com.slpolice.trafficfines.fine.service;

import com.slpolice.trafficfines.fine.dto.CreateFineRequest;
import com.slpolice.trafficfines.fine.dto.FineDTO;

import java.util.List;

/**
 * Service interface for the Fine module.
 * Interface-based design as per the implementation plan:
 * "Coupling & Cohesion — Demonstrate through package structure and interface-based design."
 *
 * All endpoints listed in the implementation plan Section 5.2 are covered here.
 */
public interface FineService {

    /**
     * Look up a fine by its reference number.
     * Supports: GET /api/fines/{referenceNumber} (Public)
     */
    FineDTO getFineByReferenceNumber(String referenceNumber);

    /**
     * Verify a fine by both reference number AND category code.
     * This is the dual-key lookup used before payment (Public).
     * Supports: GET /api/fines/verify?referenceNumber=...&categoryCode=...
     */
    FineDTO verifyFine(String referenceNumber, String categoryCode);

    /**
     * Create and issue a new fine — called by an authenticated Officer.
     * Supports: POST /api/fines (Officer role)
     *
     * @param request     the fine details submitted by the officer
     * @param officerId   the ID of the authenticated officer (extracted from JWT)
     */
    FineDTO createFine(CreateFineRequest request, Long officerId);

    /**
     * List all fines issued by a specific officer.
     * Supports: GET /api/fines/officer/{officerId} (Officer role)
     */
    List<FineDTO> getFinesByOfficer(Long officerId);
}
