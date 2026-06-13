package com.slpolice.trafficfines.export.service.impl;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.UnitValue;
import com.slpolice.trafficfines.admin.dto.CategoryReport;
import com.slpolice.trafficfines.admin.dto.DistrictReport;
import com.slpolice.trafficfines.admin.dto.FineReport;
import com.slpolice.trafficfines.admin.dto.OfficerReport;
import com.slpolice.trafficfines.admin.dto.PaymentReport;
import com.slpolice.trafficfines.export.service.ExportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PdfExportService implements ExportService {

    @Override
    public byte[] exportFinesToPdf(List<FineReport> fines) {
        return generatePdf("Fines Report", new String[]{
                "ID", "Reference No", "Officer", "Category", "Amount",
                "Driver", "Vehicle", "District", "Status", "Issued At"
        }, fines, f -> new String[]{
                String.valueOf(f.getId()), f.getReferenceNumber(), f.getOfficerName(),
                f.getCategoryCode(), fmt(f.getAmount()), f.getDriverName(),
                f.getVehicleNumber(), f.getDistrict(),
                f.getStatus().name(), str(f.getIssuedAt())
        });
    }

    @Override
    public byte[] exportPaymentsToPdf(List<PaymentReport> payments) {
        return generatePdf("Payments Report", new String[]{
                "ID", "Reference No", "Driver", "Vehicle", "Category",
                "Amount", "Method", "Channel", "Transaction Ref", "Paid At"
        }, payments, p -> new String[]{
                String.valueOf(p.getId()), p.getReferenceNumber(), p.getDriverName(),
                p.getVehicleNumber(), p.getCategoryDescription(), fmt(p.getAmountPaid()),
                p.getPaymentMethod().name(), p.getPaymentChannel().name(),
                p.getTransactionRef(), str(p.getPaidAt())
        });
    }

    @Override
    public byte[] exportDistrictsToPdf(List<DistrictReport> districts) {
        return generatePdf("District-wise Report", new String[]{
                "District", "Total Fines", "Paid", "Pending", "Expired", "Revenue (LKR)"
        }, districts, d -> new String[]{
                d.getDistrict(), String.valueOf(d.getTotalFines()),
                String.valueOf(d.getTotalPaid()), String.valueOf(d.getTotalPending()),
                String.valueOf(d.getTotalExpired()), fmt(d.getTotalRevenue())
        });
    }

    @Override
    public byte[] exportCategoriesToPdf(List<CategoryReport> categories) {
        return generatePdf("Category-wise Report", new String[]{
                "Category Code", "Description", "Fixed Amount", "Fines Issued",
                "Fines Paid", "Revenue (LKR)"
        }, categories, c -> new String[]{
                c.getCategoryCode(), c.getDescription(), fmt(c.getAmount()),
                String.valueOf(c.getTotalFinesIssued()),
                String.valueOf(c.getTotalFinesPaid()), fmt(c.getTotalRevenue())
        });
    }

    @Override
    public byte[] exportOfficersToPdf(List<OfficerReport> officers) {
        return generatePdf("Officer Report", new String[]{
                "ID", "Name", "District", "Phone", "Fines Issued", "Fines Paid"
        }, officers, o -> new String[]{
                String.valueOf(o.getId()), o.getFullName(), o.getDistrict(),
                o.getPhoneNumber(), String.valueOf(o.getTotalFinesIssued()),
                String.valueOf(o.getTotalFinesPaid())
        });
    }

    @Override
    public byte[] exportFinesToCsv(List<FineReport> fines) {
        throw new UnsupportedOperationException("CSV export is handled by CsvExportService");
    }

    @Override
    public byte[] exportPaymentsToCsv(List<PaymentReport> payments) {
        throw new UnsupportedOperationException("CSV export is handled by CsvExportService");
    }

    @Override
    public byte[] exportDistrictsToCsv(List<DistrictReport> districts) {
        throw new UnsupportedOperationException("CSV export is handled by CsvExportService");
    }

    @Override
    public byte[] exportCategoriesToCsv(List<CategoryReport> categories) {
        throw new UnsupportedOperationException("CSV export is handled by CsvExportService");
    }

    @Override
    public byte[] exportOfficersToCsv(List<OfficerReport> officers) {
        throw new UnsupportedOperationException("CSV export is handled by CsvExportService");
    }

    private <T> byte[] generatePdf(String title, String[] headers, List<T> rows, RowMapper<T> mapper) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (PdfDocument pdf = new PdfDocument(new PdfWriter(baos));
             Document doc = new Document(pdf)) {

            doc.add(new Paragraph(title).setFontSize(18).setBold());
            doc.add(new Paragraph("Generated: " + java.time.LocalDateTime.now())
                    .setFontSize(10).setMarginBottom(20));

            Table table = new Table(UnitValue.createPercentArray(headers.length)).useAllAvailableWidth();
            for (String h : headers) {
                table.addHeaderCell(new Cell().add(new Paragraph(h).setBold().setFontSize(9)));
            }
            for (T row : rows) {
                for (String val : mapper.map(row)) {
                    table.addCell(new Cell().add(new Paragraph(val).setFontSize(8)));
                }
            }
            doc.add(table);
        } catch (Exception e) {
            log.error("Failed to generate PDF: {}", e.getMessage());
            throw new RuntimeException("Failed to generate PDF", e);
        }
        return baos.toByteArray();
    }

    private String fmt(java.math.BigDecimal d) {
        return d != null ? String.format("%.2f", d) : "0.00";
    }

    private String str(Object o) {
        return o != null ? o.toString() : "";
    }

    @FunctionalInterface
    private interface RowMapper<T> {
        String[] map(T row);
    }
}
