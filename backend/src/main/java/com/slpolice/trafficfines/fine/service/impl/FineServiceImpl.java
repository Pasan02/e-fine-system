package com.slpolice.trafficfines.fine.service.impl;

import com.slpolice.trafficfines.auth.entity.User;
import com.slpolice.trafficfines.auth.repository.UserRepository;
import com.slpolice.trafficfines.fine.dto.CreateFineRequest;
import com.slpolice.trafficfines.fine.dto.FineDTO;
import com.slpolice.trafficfines.fine.entity.Fine;
import com.slpolice.trafficfines.fine.entity.FineCategory;
import com.slpolice.trafficfines.fine.entity.FineStatus;
import com.slpolice.trafficfines.fine.repository.FineCategoryRepository;
import com.slpolice.trafficfines.fine.repository.FineRepository;
import com.slpolice.trafficfines.fine.service.FineService;
import com.slpolice.trafficfines.shared.exception.BadRequestException;
import com.slpolice.trafficfines.shared.exception.ResourceNotFoundException;
import com.slpolice.trafficfines.shared.util.ReferenceNumberGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of the FineService.
 * Follows the Clean Architecture pattern: Controller → Service → Repository.
 * Uses @Transactional to ensure database consistency for write operations.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class FineServiceImpl implements FineService {

    private final FineRepository fineRepository;
    private final FineCategoryRepository fineCategoryRepository;
    private final UserRepository userRepository;
    private final ReferenceNumberGenerator referenceNumberGenerator;

    @Override
    @Transactional(readOnly = true)
    public FineDTO getFineByReferenceNumber(String referenceNumber) {
        log.debug("Looking up fine with reference number: {}", referenceNumber);
        Fine fine = fineRepository.findByReferenceNumber(referenceNumber)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Fine not found with reference number: " + referenceNumber));
        return FineDTO.from(fine);
    }

    @Override
    @Transactional(readOnly = true)
    public FineDTO verifyFine(String referenceNumber, String categoryCode) {
        log.debug("Verifying fine: referenceNumber={}, categoryCode={}", referenceNumber, categoryCode);
        Fine fine = fineRepository
                .findByReferenceNumberAndCategoryCategoryCode(referenceNumber, categoryCode)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No fine found matching the provided reference number and category code."));

        // A fine can only be paid if it is still PENDING
        if (fine.getStatus() == FineStatus.PAID) {
            throw new BadRequestException("This fine has already been paid.");
        }
        if (fine.getStatus() == FineStatus.EXPIRED) {
            throw new BadRequestException("This fine has expired and can no longer be paid online.");
        }

        return FineDTO.from(fine);
    }

    @Override
    @Transactional
    public FineDTO createFine(CreateFineRequest request, Long officerId) {
        log.info("Officer {} is issuing a new fine for vehicle {}", officerId, request.getVehicleNumber());

        // Validate officer exists and has OFFICER role
        User officer = userRepository.findById(officerId)
                .orElseThrow(() -> new ResourceNotFoundException("Officer not found with id: " + officerId));

        // Validate category exists and is active
        FineCategory category = fineCategoryRepository
                .findByCategoryCodeAndIsActiveTrue(request.getCategoryCode())
                .orElseThrow(() -> new BadRequestException(
                        "Invalid or inactive fine category code: " + request.getCategoryCode()));

        // Generate unique reference number: TF-YYYY-DISTRICT-NNNNN
        String referenceNumber = referenceNumberGenerator.generate(request.getDistrict());

        Fine fine = Fine.builder()
                .referenceNumber(referenceNumber)
                .officer(officer)
                .category(category)
                .driverLicenseNo(request.getDriverLicenseNo())
                .driverName(request.getDriverName())
                .vehicleNumber(request.getVehicleNumber())
                .district(request.getDistrict())
                .location(request.getLocation())
                .status(FineStatus.PENDING)
                .build();

        Fine savedFine = fineRepository.save(fine);
        log.info("Fine created successfully with reference number: {}", savedFine.getReferenceNumber());

        return FineDTO.from(savedFine);
    }

    @Override
    @Transactional(readOnly = true)
    public List<FineDTO> getFinesByOfficer(Long officerId) {
        log.debug("Fetching fines for officer id: {}", officerId);
        // Validate officer exists
        if (!userRepository.existsById(officerId)) {
            throw new ResourceNotFoundException("Officer not found with id: " + officerId);
        }
        return fineRepository.findByOfficerId(officerId)
                .stream()
                .map(FineDTO::from)
                .collect(Collectors.toList());
    }
}
