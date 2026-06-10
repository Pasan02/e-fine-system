package com.slpolice.trafficfines.fine.service;

import com.slpolice.trafficfines.auth.entity.Role;
import com.slpolice.trafficfines.auth.entity.User;
import com.slpolice.trafficfines.auth.repository.UserRepository;
import com.slpolice.trafficfines.fine.dto.CreateFineRequest;
import com.slpolice.trafficfines.fine.dto.FineDTO;
import com.slpolice.trafficfines.fine.entity.Fine;
import com.slpolice.trafficfines.fine.entity.FineCategory;
import com.slpolice.trafficfines.fine.entity.FineStatus;
import com.slpolice.trafficfines.fine.repository.FineCategoryRepository;
import com.slpolice.trafficfines.fine.repository.FineRepository;
import com.slpolice.trafficfines.fine.service.impl.FineServiceImpl;
import com.slpolice.trafficfines.shared.exception.BadRequestException;
import com.slpolice.trafficfines.shared.exception.ResourceNotFoundException;
import com.slpolice.trafficfines.shared.util.ReferenceNumberGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Unit tests for FineServiceImpl.
 * As specified in the implementation plan:
 *   "Unit Tests: JUnit 5 + Mockito for service layer (Members 1 & 2)"
 *
 * Uses @ExtendWith(MockitoExtension.class) — pure unit tests, no Spring context loaded.
 * All repositories are mocked via @Mock.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("FineServiceImpl Unit Tests")
class FineServiceImplTest {

    // ── Mocks ────────────────────────────────────────────────────────────────
    @Mock private FineRepository fineRepository;
    @Mock private FineCategoryRepository fineCategoryRepository;
    @Mock private UserRepository userRepository;
    @Mock private ReferenceNumberGenerator referenceNumberGenerator;

    // ── System Under Test ────────────────────────────────────────────────────
    @InjectMocks
    private FineServiceImpl fineService;

    // ── Test Data ────────────────────────────────────────────────────────────
    private User officer;
    private FineCategory category;
    private Fine fine;

    @BeforeEach
    void setUp() {
        officer = User.builder()
                .id(1L)
                .username("officer1")
                .fullName("P. K. Silva")
                .phoneNumber("0771234567")
                .role(Role.OFFICER)
                .district("WP")
                .build();

        category = FineCategory.builder()
                .id(1L)
                .categoryCode("SPD01")
                .description("Exceeding speed limit in urban area")
                .amount(new BigDecimal("1500.00"))
                .isActive(true)
                .build();

        fine = Fine.builder()
                .id(1L)
                .referenceNumber("TF-2026-WP-00001")
                .officer(officer)
                .category(category)
                .driverLicenseNo("B1234567")
                .driverName("A. B. Perera")
                .vehicleNumber("CAR-1234")
                .district("WP")
                .location("Colombo 03")
                .status(FineStatus.PENDING)
                .build();
    }

    // =========================================================================
    // getFineByReferenceNumber
    // =========================================================================
    @Nested
    @DisplayName("getFineByReferenceNumber()")
    class GetFineByReferenceNumber {

        @Test
        @DisplayName("Should return FineDTO when fine exists")
        void shouldReturnFineDtoWhenFound() {
            when(fineRepository.findByReferenceNumber("TF-2026-WP-00001"))
                    .thenReturn(Optional.of(fine));

            FineDTO result = fineService.getFineByReferenceNumber("TF-2026-WP-00001");

            assertThat(result).isNotNull();
            assertThat(result.getReferenceNumber()).isEqualTo("TF-2026-WP-00001");
            assertThat(result.getDriverName()).isEqualTo("A. B. Perera");
            assertThat(result.getCategoryCode()).isEqualTo("SPD01");
            assertThat(result.getStatus()).isEqualTo(FineStatus.PENDING);
            verify(fineRepository, times(1)).findByReferenceNumber("TF-2026-WP-00001");
        }

        @Test
        @DisplayName("Should throw ResourceNotFoundException when fine not found")
        void shouldThrowWhenFineNotFound() {
            when(fineRepository.findByReferenceNumber("UNKNOWN-REF"))
                    .thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class,
                    () -> fineService.getFineByReferenceNumber("UNKNOWN-REF"));
        }
    }

    // =========================================================================
    // verifyFine
    // =========================================================================
    @Nested
    @DisplayName("verifyFine()")
    class VerifyFine {

        @Test
        @DisplayName("Should return FineDTO when fine is PENDING")
        void shouldReturnFineDtoWhenPending() {
            when(fineRepository.findByReferenceNumberAndCategoryCategoryCode(
                    "TF-2026-WP-00001", "SPD01"))
                    .thenReturn(Optional.of(fine));

            FineDTO result = fineService.verifyFine("TF-2026-WP-00001", "SPD01");

            assertThat(result).isNotNull();
            assertThat(result.getAmount()).isEqualByComparingTo("1500.00");
        }

        @Test
        @DisplayName("Should throw ResourceNotFoundException when no match found")
        void shouldThrowWhenNoMatch() {
            when(fineRepository.findByReferenceNumberAndCategoryCategoryCode(anyString(), anyString()))
                    .thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class,
                    () -> fineService.verifyFine("WRONG-REF", "WRONG-CAT"));
        }

        @Test
        @DisplayName("Should throw BadRequestException when fine is already PAID")
        void shouldThrowWhenAlreadyPaid() {
            fine.setStatus(FineStatus.PAID);
            when(fineRepository.findByReferenceNumberAndCategoryCategoryCode(
                    "TF-2026-WP-00001", "SPD01"))
                    .thenReturn(Optional.of(fine));

            BadRequestException ex = assertThrows(BadRequestException.class,
                    () -> fineService.verifyFine("TF-2026-WP-00001", "SPD01"));

            assertThat(ex.getMessage()).containsIgnoringCase("already been paid");
        }

        @Test
        @DisplayName("Should throw BadRequestException when fine is EXPIRED")
        void shouldThrowWhenExpired() {
            fine.setStatus(FineStatus.EXPIRED);
            when(fineRepository.findByReferenceNumberAndCategoryCategoryCode(
                    "TF-2026-WP-00001", "SPD01"))
                    .thenReturn(Optional.of(fine));

            BadRequestException ex = assertThrows(BadRequestException.class,
                    () -> fineService.verifyFine("TF-2026-WP-00001", "SPD01"));

            assertThat(ex.getMessage()).containsIgnoringCase("expired");
        }
    }

    // =========================================================================
    // createFine
    // =========================================================================
    @Nested
    @DisplayName("createFine()")
    class CreateFine {

        private CreateFineRequest request;

        @BeforeEach
        void setUpRequest() {
            request = new CreateFineRequest();
            request.setCategoryCode("SPD01");
            request.setDriverLicenseNo("B1234567");
            request.setDriverName("A. B. Perera");
            request.setVehicleNumber("CAR1234");
            request.setDistrict("WP");
            request.setLocation("Colombo 03");
        }

        @Test
        @DisplayName("Should create and return a new fine successfully")
        void shouldCreateFineSuccessfully() {
            when(userRepository.findById(1L)).thenReturn(Optional.of(officer));
            when(fineCategoryRepository.findByCategoryCodeAndIsActiveTrue("SPD01"))
                    .thenReturn(Optional.of(category));
            when(referenceNumberGenerator.generate("WP")).thenReturn("TF-2026-WP-00001");
            when(fineRepository.save(any(Fine.class))).thenReturn(fine);

            FineDTO result = fineService.createFine(request, 1L);

            assertThat(result).isNotNull();
            assertThat(result.getReferenceNumber()).isEqualTo("TF-2026-WP-00001");
            assertThat(result.getStatus()).isEqualTo(FineStatus.PENDING);
            verify(fineRepository, times(1)).save(any(Fine.class));
        }

        @Test
        @DisplayName("Should throw ResourceNotFoundException when officer not found")
        void shouldThrowWhenOfficerNotFound() {
            when(userRepository.findById(99L)).thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class,
                    () -> fineService.createFine(request, 99L));

            verify(fineRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should throw BadRequestException when category code is invalid")
        void shouldThrowWhenCategoryInvalid() {
            when(userRepository.findById(1L)).thenReturn(Optional.of(officer));
            when(fineCategoryRepository.findByCategoryCodeAndIsActiveTrue("INVALID"))
                    .thenReturn(Optional.empty());

            request.setCategoryCode("INVALID");

            assertThrows(BadRequestException.class,
                    () -> fineService.createFine(request, 1L));

            verify(fineRepository, never()).save(any());
        }
    }

    // =========================================================================
    // getFinesByOfficer
    // =========================================================================
    @Nested
    @DisplayName("getFinesByOfficer()")
    class GetFinesByOfficer {

        @Test
        @DisplayName("Should return list of fines for a valid officer")
        void shouldReturnFinesForOfficer() {
            when(userRepository.existsById(1L)).thenReturn(true);
            when(fineRepository.findByOfficerId(1L)).thenReturn(List.of(fine));

            List<FineDTO> results = fineService.getFinesByOfficer(1L);

            assertThat(results).hasSize(1);
            assertThat(results.get(0).getOfficerId()).isEqualTo(1L);
        }

        @Test
        @DisplayName("Should return empty list when officer has no fines")
        void shouldReturnEmptyListWhenNoFines() {
            when(userRepository.existsById(1L)).thenReturn(true);
            when(fineRepository.findByOfficerId(1L)).thenReturn(List.of());

            List<FineDTO> results = fineService.getFinesByOfficer(1L);

            assertThat(results).isEmpty();
        }

        @Test
        @DisplayName("Should throw ResourceNotFoundException when officer not found")
        void shouldThrowWhenOfficerNotFound() {
            when(userRepository.existsById(99L)).thenReturn(false);

            assertThrows(ResourceNotFoundException.class,
                    () -> fineService.getFinesByOfficer(99L));
        }
    }
}
