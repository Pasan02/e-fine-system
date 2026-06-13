package com.slpolice.trafficfines.export.service;

import com.slpolice.trafficfines.admin.dto.CategoryReport;
import com.slpolice.trafficfines.admin.dto.DistrictReport;
import com.slpolice.trafficfines.admin.dto.FineReport;
import com.slpolice.trafficfines.admin.dto.OfficerReport;
import com.slpolice.trafficfines.admin.dto.PaymentReport;

import java.util.List;

public interface ExportService {

    byte[] exportFinesToCsv(List<FineReport> fines);

    byte[] exportFinesToPdf(List<FineReport> fines);

    byte[] exportPaymentsToCsv(List<PaymentReport> payments);

    byte[] exportPaymentsToPdf(List<PaymentReport> payments);

    byte[] exportDistrictsToCsv(List<DistrictReport> districts);

    byte[] exportDistrictsToPdf(List<DistrictReport> districts);

    byte[] exportCategoriesToCsv(List<CategoryReport> categories);

    byte[] exportCategoriesToPdf(List<CategoryReport> categories);

    byte[] exportOfficersToCsv(List<OfficerReport> officers);

    byte[] exportOfficersToPdf(List<OfficerReport> officers);
}
