package com.slpolice.trafficfines.export.service.impl;

import com.slpolice.trafficfines.admin.dto.CategoryReport;
import com.slpolice.trafficfines.admin.dto.DistrictReport;
import com.slpolice.trafficfines.admin.dto.FineReport;
import com.slpolice.trafficfines.admin.dto.OfficerReport;
import com.slpolice.trafficfines.admin.dto.PaymentReport;
import com.slpolice.trafficfines.export.service.ExportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.StringWriter;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CsvExportService implements ExportService {

    @Override
    public byte[] exportFinesToCsv(List<FineReport> fines) {
        StringWriter sw = new StringWriter();
        sw.write("ID,Reference No,Officer,Officer District,Category Code,Category,Amount,License No,Driver,Vehicle,District,Location,Status,Issued At\n");
        for (FineReport f : fines) {
            sw.write(String.format("%d,%s,%s,%s,%s,%s,%.2f,%s,%s,%s,%s,%s,%s,%s\n",
                    f.getId(), csv(f.getReferenceNumber()), csv(f.getOfficerName()),
                    csv(f.getOfficerDistrict()), csv(f.getCategoryCode()),
                    csv(f.getCategoryDescription()), f.getAmount(),
                    csv(f.getDriverLicenseNo()), csv(f.getDriverName()),
                    csv(f.getVehicleNumber()), csv(f.getDistrict()),
                    csv(f.getLocation()), f.getStatus(), f.getIssuedAt()));
        }
        return sw.toString().getBytes();
    }

    @Override
    public byte[] exportPaymentsToCsv(List<PaymentReport> payments) {
        StringWriter sw = new StringWriter();
        sw.write("ID,Reference No,Driver,Vehicle,Category,Amount,Method,Channel,Transaction Ref,Paid At\n");
        for (PaymentReport p : payments) {
            sw.write(String.format("%d,%s,%s,%s,%s,%.2f,%s,%s,%s,%s\n",
                    p.getId(), csv(p.getReferenceNumber()), csv(p.getDriverName()),
                    csv(p.getVehicleNumber()), csv(p.getCategoryDescription()),
                    p.getAmountPaid(), p.getPaymentMethod(), p.getPaymentChannel(),
                    csv(p.getTransactionRef()), p.getPaidAt()));
        }
        return sw.toString().getBytes();
    }

    @Override
    public byte[] exportDistrictsToCsv(List<DistrictReport> districts) {
        StringWriter sw = new StringWriter();
        sw.write("District,Total Fines,Paid,Pending,Expired,Revenue (LKR)\n");
        for (DistrictReport d : districts) {
            sw.write(String.format("%s,%d,%d,%d,%d,%.2f\n",
                    csv(d.getDistrict()), d.getTotalFines(), d.getTotalPaid(),
                    d.getTotalPending(), d.getTotalExpired(), d.getTotalRevenue()));
        }
        return sw.toString().getBytes();
    }

    @Override
    public byte[] exportCategoriesToCsv(List<CategoryReport> categories) {
        StringWriter sw = new StringWriter();
        sw.write("Category Code,Description,Fixed Amount (LKR),Fines Issued,Fines Paid,Revenue (LKR)\n");
        for (CategoryReport c : categories) {
            sw.write(String.format("%s,%s,%.2f,%d,%d,%.2f\n",
                    csv(c.getCategoryCode()), csv(c.getDescription()),
                    c.getAmount(), c.getTotalFinesIssued(),
                    c.getTotalFinesPaid(), c.getTotalRevenue()));
        }
        return sw.toString().getBytes();
    }

    @Override
    public byte[] exportOfficersToCsv(List<OfficerReport> officers) {
        StringWriter sw = new StringWriter();
        sw.write("ID,Name,District,Phone,Fines Issued,Fines Paid\n");
        for (OfficerReport o : officers) {
            sw.write(String.format("%d,%s,%s,%s,%d,%d\n",
                    o.getId(), csv(o.getFullName()), csv(o.getDistrict()),
                    csv(o.getPhoneNumber()), o.getTotalFinesIssued(),
                    o.getTotalFinesPaid()));
        }
        return sw.toString().getBytes();
    }

    @Override
    public byte[] exportFinesToPdf(List<FineReport> fines) {
        throw new UnsupportedOperationException("PDF export is handled by PdfExportService");
    }

    @Override
    public byte[] exportPaymentsToPdf(List<PaymentReport> payments) {
        throw new UnsupportedOperationException("PDF export is handled by PdfExportService");
    }

    @Override
    public byte[] exportDistrictsToPdf(List<DistrictReport> districts) {
        throw new UnsupportedOperationException("PDF export is handled by PdfExportService");
    }

    @Override
    public byte[] exportCategoriesToPdf(List<CategoryReport> categories) {
        throw new UnsupportedOperationException("PDF export is handled by PdfExportService");
    }

    @Override
    public byte[] exportOfficersToPdf(List<OfficerReport> officers) {
        throw new UnsupportedOperationException("PDF export is handled by PdfExportService");
    }

    private String csv(String value) {
        if (value == null) return "";
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }
}
