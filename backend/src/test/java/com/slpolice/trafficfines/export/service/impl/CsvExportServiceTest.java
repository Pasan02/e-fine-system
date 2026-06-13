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

@DisplayName("CsvExportService Unit Tests")
class CsvExportServiceTest {

    private CsvExportService csvExportService;

    @BeforeEach
    void setUp() {
        csvExportService = new CsvExportService();
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
    @DisplayName("exportFinesToCsv()")
    class ExportFinesToCsv {

        @Test
        @DisplayName("Should generate CSV with header and data row")
        void shouldGenerateCsvWithHeaderAndRow() {
            byte[] result = csvExportService.exportFinesToCsv(List.of(sampleFine()));
            String csv = new String(result);
            String[] lines = csv.split("\n");

            assertThat(lines).hasSize(2);
            assertThat(lines[0]).isEqualTo("ID,Reference No,Officer,Officer District,Category Code,Category,Amount,License No,Driver,Vehicle,District,Location,Status,Issued At");
            assertThat(lines[1]).contains("TF-2026-WP-00001");
            assertThat(lines[1]).contains("PAID");
        }

        @Test
        @DisplayName("Should return only header for empty list")
        void shouldReturnOnlyHeaderForEmptyList() {
            byte[] result = csvExportService.exportFinesToCsv(Collections.emptyList());
            String csv = new String(result);
            assertThat(csv.split("\n")).hasSize(1);
        }

        @Test
        @DisplayName("Should handle commas and quotes in values")
        void shouldHandleCommasAndQuotes() {
            FineReport fine = FineReport.builder()
                    .id(2L)
                    .referenceNumber("TF-2026-WP-00002")
                    .officerName("Silva, W. A.")
                    .officerDistrict("WP")
                    .categoryCode("SPD01")
                    .categoryDescription("Speed, \"limit\" violation")
                    .amount(new BigDecimal("1000.00"))
                    .driverLicenseNo("B7654321")
                    .driverName("Perera, A.")
                    .vehicleNumber("BUS-5678")
                    .district("WP")
                    .location("Main St, Colombo")
                    .status(FineStatus.PENDING)
                    .issuedAt(LocalDateTime.of(2026, 2, 1, 9, 0))
                    .build();

            byte[] result = csvExportService.exportFinesToCsv(List.of(fine));
            String csv = new String(result);
            String[] lines = csv.split("\n");

            assertThat(lines[1]).contains("\"Silva, W. A.\"");
            assertThat(lines[1]).contains("\"Speed, \"\"limit\"\" violation\"");
            assertThat(lines[1]).contains("\"Perera, A.\"");
            assertThat(lines[1]).contains("\"Main St, Colombo\"");
        }
    }

    @Nested
    @DisplayName("exportPaymentsToCsv()")
    class ExportPaymentsToCsv {

        @Test
        @DisplayName("Should generate CSV with header and data row")
        void shouldGenerateCsvWithHeaderAndRow() {
            byte[] result = csvExportService.exportPaymentsToCsv(List.of(samplePayment()));
            String csv = new String(result);
            String[] lines = csv.split("\n");

            assertThat(lines).hasSize(2);
            assertThat(lines[0]).isEqualTo("ID,Reference No,Driver,Vehicle,Category,Amount,Method,Channel,Transaction Ref,Paid At");
            assertThat(lines[1]).contains("TXN-ABC-123");
            assertThat(lines[1]).contains("CARD");
            assertThat(lines[1]).contains("WEB_PORTAL");
        }
    }

    @Nested
    @DisplayName("exportDistrictsToCsv()")
    class ExportDistrictsToCsv {

        @Test
        @DisplayName("Should generate CSV with header and data row")
        void shouldGenerateCsvWithHeaderAndRow() {
            byte[] result = csvExportService.exportDistrictsToCsv(List.of(sampleDistrict()));
            String csv = new String(result);
            String[] lines = csv.split("\n");

            assertThat(lines).hasSize(2);
            assertThat(lines[0]).isEqualTo("District,Total Fines,Paid,Pending,Expired,Revenue (LKR)");
            assertThat(lines[1]).contains("WP,60,40,15,5,60000.00");
        }
    }

    @Nested
    @DisplayName("exportCategoriesToCsv()")
    class ExportCategoriesToCsv {

        @Test
        @DisplayName("Should generate CSV with header and data row")
        void shouldGenerateCsvWithHeaderAndRow() {
            byte[] result = csvExportService.exportCategoriesToCsv(List.of(sampleCategory()));
            String csv = new String(result);
            String[] lines = csv.split("\n");

            assertThat(lines).hasSize(2);
            assertThat(lines[0]).isEqualTo("Category Code,Description,Fixed Amount (LKR),Fines Issued,Fines Paid,Revenue (LKR)");
            assertThat(lines[1]).contains("SPD01");
            assertThat(lines[1]).contains("60000.00");
        }
    }

    @Nested
    @DisplayName("exportOfficersToCsv()")
    class ExportOfficersToCsv {

        @Test
        @DisplayName("Should generate CSV with header and data row")
        void shouldGenerateCsvWithHeaderAndRow() {
            byte[] result = csvExportService.exportOfficersToCsv(List.of(sampleOfficer()));
            String csv = new String(result);
            String[] lines = csv.split("\n");

            assertThat(lines).hasSize(2);
            assertThat(lines[0]).isEqualTo("ID,Name,District,Phone,Fines Issued,Fines Paid");
            assertThat(lines[1]).contains("P. K. Silva");
            assertThat(lines[1]).contains("20,15");
        }
    }

    @Nested
    @DisplayName("PDF methods (unsupported)")
    class PdfMethods {

        @Test
        @DisplayName("Should throw UnsupportedOperationException for PDF methods")
        void shouldThrowForPdfMethods() {
            assertThatThrownBy(() -> csvExportService.exportFinesToPdf(List.of(sampleFine())))
                    .isInstanceOf(UnsupportedOperationException.class);
            assertThatThrownBy(() -> csvExportService.exportPaymentsToPdf(List.of(samplePayment())))
                    .isInstanceOf(UnsupportedOperationException.class);
            assertThatThrownBy(() -> csvExportService.exportDistrictsToPdf(List.of(sampleDistrict())))
                    .isInstanceOf(UnsupportedOperationException.class);
            assertThatThrownBy(() -> csvExportService.exportCategoriesToPdf(List.of(sampleCategory())))
                    .isInstanceOf(UnsupportedOperationException.class);
            assertThatThrownBy(() -> csvExportService.exportOfficersToPdf(List.of(sampleOfficer())))
                    .isInstanceOf(UnsupportedOperationException.class);
        }
    }
}
