package com.slpolice.trafficfines.fine.controller;

import com.slpolice.trafficfines.fine.dto.CreateFineRequest;
import com.slpolice.trafficfines.fine.dto.FineDTO;
import com.slpolice.trafficfines.fine.service.FineService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import com.slpolice.trafficfines.auth.repository.UserRepository;
import com.slpolice.trafficfines.shared.exception.ResourceNotFoundException;

import java.util.List;

/**
 * REST Controller for Fine endpoints.
 * All endpoints are as defined in the implementation plan Section 5.2:
 *
 *   GET  /api/fines/{referenceNumber}     — Public (look up by reference number)
 *   GET  /api/fines/verify                — Public (dual-key verify before payment)
 *   POST /api/fines                       — Officer only (create/issue a new fine)
 *   GET  /api/fines/officer/{officerId}   — Officer only (list officer's fines)
 */
@RestController
@RequestMapping("/api/fines")
@RequiredArgsConstructor
public class FineController {

    private final FineService fineService;
    private final UserRepository userRepository;

    /**
     * GET /api/fines/{referenceNumber}
     * Public endpoint — driver looks up a fine by the reference number on their fine sheet.
     */
    @GetMapping("/{referenceNumber}")
    public ResponseEntity<FineDTO> getFineByReferenceNumber(@PathVariable String referenceNumber) {
        return ResponseEntity.ok(fineService.getFineByReferenceNumber(referenceNumber));
    }

    /**
     * GET /api/fines/verify?referenceNumber=TF-2026-WP-00001&categoryCode=SPD01
     * Public endpoint — verifies the fine using both identifiers before payment.
     * Returns the fine details (including amount) so the driver can confirm before paying.
     */
    @GetMapping("/verify")
    public ResponseEntity<FineDTO> verifyFine(
            @RequestParam String referenceNumber,
            @RequestParam String categoryCode) {
        return ResponseEntity.ok(fineService.verifyFine(referenceNumber, categoryCode));
    }

    /**
     * POST /api/fines
     * Officer role only — issues a new traffic fine to a driver.
     * The officer ID is extracted from the JWT token (no need to pass in the request body).
     */
    @PostMapping
    @PreAuthorize("hasRole('OFFICER')")
    public ResponseEntity<FineDTO> createFine(
            @Valid @RequestBody CreateFineRequest request,
            Authentication authentication) {

        Long officerId = getOfficerIdFromAuthentication(authentication);
        FineDTO createdFine = fineService.createFine(request, officerId);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdFine);
    }

    /**
     * GET /api/fines/officer/{officerId}
     * Officer role only — lists all fines issued by the specified officer.
     */
    @GetMapping("/officer/{officerId}")
    @PreAuthorize("hasRole('OFFICER') or hasRole('ADMIN')")
    public ResponseEntity<List<FineDTO>> getFinesByOfficer(@PathVariable Long officerId) {
        return ResponseEntity.ok(fineService.getFinesByOfficer(officerId));
    }

    // -----------------------------------------------------------------------
    // Helper
    // -----------------------------------------------------------------------

    /**
     * Extracts the officer's database ID from the authenticated JWT principal.
     * The username stored in the JWT is looked up in the database to resolve the ID.
     */
    private Long getOfficerIdFromAuthentication(Authentication authentication) {
        String username = ((UserDetails) authentication.getPrincipal()).getUsername();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Authenticated user not found"))
                .getId();
    }
}
