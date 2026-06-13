package com.slpolice.trafficfines.export.service.impl;

import com.slpolice.trafficfines.admin.dto.CategoryReport;
import com.slpolice.trafficfines.admin.dto.DistrictReport;
import com.slpolice.trafficfines.admin.dto.FineReport;
import com.slpolice.trafficfines.admin.dto.OfficerReport;
import com.slpolice.trafficfines.admin.dto.PaymentReport;
import com.slpolice.trafficfines.fine.entity.FineStatus;
import com.slpolice.trafficfines.payment.entity.PaymentChannel;
import com.slpolice.trafficfines.payment.entity.PaymentMethod;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("PdfExportService Unit Tests")
class PdfExportServiceTest {

    private PdfExportService pdfExportService;

    @BeforeEach
    void setUp() {
        pdfExportService = new PdfExportService();
    }

    private FineReport sampleFine() {
        return FineReport.builder()
                .id(1L)
                .referenceNumber("TF-2026-WP-00001")
                .officerName("P. K. Silva")
                .officerDistrict("WP")
                .categoryCode("SPD01")
                .categoryDescription("Exceeding speed limit")
                .amount(new BigDecimal("1500.00"))
                .driverLicenseNo("B1234567")
                .driverName("A. B. Perera")
                .vehicleNumber("CAR-1234")
                .district("WP")
                .location("Colombo 03")
                .status(FineStatus.PAID)
                .issuedAt(LocalDateTime.of(2026, 1, 15, 10, 30))
                .build();
    }

    private PaymentReport samplePayment() {
        return PaymentReport.builder()
                .id(10L)
                .referenceNumber("TF-2026-WP-00001")
                .driverName("A. B. Perera")
                .vehicleNumber("CAR-1234")
                .categoryDescription("Exceeding speed limit")
                .amountPaid(new BigDecimal("1500.00"))
                .paymentMethod(PaymentMethod.CARD)
                .paymentChannel(PaymentChannel.WEB_PORTAL)
                .transactionRef("TXN-ABC-123")
                .paidAt(LocalDateTime.of(2026, 1, 15, 12, 0))
                .build();
    }

    private DistrictReport sampleDistrict() {
        return DistrictReport.builder()
                .district("WP")
                .totalFines(60L)
                .totalPaid(40L)
                .totalPending(15L)
                .totalExpired(5L)
                .totalRevenue(new BigDecimal("60000.00"))
                .build();
    }

    private CategoryReport sampleCategory() {
        return CategoryReport.builder()
                .categoryCode("SPD01")
                .description("Exceeding speed limit")
                .amount(new BigDecimal("1500.00"))
                .totalFinesIssued(60L)
                .totalFinesPaid(40L)
                .totalRevenue(new BigDecimal("60000.00"))
                .build();
    }

    private OfficerReport sampleOfficer() {
        return OfficerReport.builder()
                .id(1L)
                .fullName("P. K. Silva")
                .district("WP")
                .phoneNumber("0771234567")
                .totalFinesIssued(20L)
                .totalFinesPaid(15L)
                .build();
    }

    @Nested
    @DisplayName("exportFinesToPdf()")
    class ExportFinesToPdf {

        @Test
        @DisplayName("Should generate valid PDF with fine data")
        void shouldGenerateValidPdf() {
            byte[] result = pdfExportService.exportFinesToPdf(List.of(sampleFine()));

            assertThat(result).isNotEmpty();
            assertThat(new String(result, 0, 4)).isEqualTo("%PDF");
        }

        @Test
        @DisplayName("Should generate valid PDF for empty list")
        void shouldGeneratePdfForEmptyList() {
            byte[] result = pdfExportService.exportFinesToPdf(Collections.emptyList());

            assertThat(result).isNotEmpty();
            assertThat(new String(result, 0, 4)).isEqualTo("%PDF");
        }
    }

    @Nested
    @DisplayName("exportPaymentsToPdf()")
    class ExportPaymentsToPdf {

        @Test
        @DisplayName("Should generate valid PDF with payment data")
        void shouldGenerateValidPdf() {
            byte[] result = pdfExportService.exportPaymentsToPdf(List.of(samplePayment()));

            assertThat(result).isNotEmpty();
            assertThat(new String(result, 0, 4)).isEqualTo("%PDF");
        }
    }

    @Nested
    @DisplayName("exportDistrictsToPdf()")
    class ExportDistrictsToPdf {

        @Test
        @DisplayName("Should generate valid PDF with district data")
        void shouldGenerateValidPdf() {
            byte[] result = pdfExportService.exportDistrictsToPdf(List.of(sampleDistrict()));

            assertThat(result).isNotEmpty();
            assertThat(new String(result, 0, 4)).isEqualTo("%PDF");
        }
    }

    @Nested
    @DisplayName("exportCategoriesToPdf()")
    class ExportCategoriesToPdf {

        @Test
        @DisplayName("Should generate valid PDF with category data")
        void shouldGenerateValidPdf() {
            byte[] result = pdfExportService.exportCategoriesToPdf(List.of(sampleCategory()));

            assertThat(result).isNotEmpty();
            assertThat(new String(result, 0, 4)).isEqualTo("%PDF");
        }
    }

    @Nested
    @DisplayName("exportOfficersToPdf()")
    class ExportOfficersToPdf {

        @Test
        @DisplayName("Should generate valid PDF with officer data")
        void shouldGenerateValidPdf() {
            byte[] result = pdfExportService.exportOfficersToPdf(List.of(sampleOfficer()));

            assertThat(result).isNotEmpty();
            assertThat(new String(result, 0, 4)).isEqualTo("%PDF");
        }
    }

    @Nested
    @DisplayName("CSV methods (unsupported)")
    class CsvMethods {

        @Test
        @DisplayName("Should throw UnsupportedOperationException for CSV methods")
        void shouldThrowForCsvMethods() {
            assertThatThrownBy(() -> pdfExportService.exportFinesToCsv(List.of(sampleFine())))
                    .isInstanceOf(UnsupportedOperationException.class);
            assertThatThrownBy(() -> pdfExportService.exportPaymentsToCsv(List.of(samplePayment())))
                    .isInstanceOf(UnsupportedOperationException.class);
            assertThatThrownBy(() -> pdfExportService.exportDistrictsToCsv(List.of(sampleDistrict())))
                    .isInstanceOf(UnsupportedOperationException.class);
            assertThatThrownBy(() -> pdfExportService.exportCategoriesToCsv(List.of(sampleCategory())))
                    .isInstanceOf(UnsupportedOperationException.class);
            assertThatThrownBy(() -> pdfExportService.exportOfficersToCsv(List.of(sampleOfficer())))
                    .isInstanceOf(UnsupportedOperationException.class);
        }
    }
}
