package com.slpolice.trafficfines.export.controller;

import com.slpolice.trafficfines.admin.service.AdminService;
import com.slpolice.trafficfines.export.service.ExportService;
import com.slpolice.trafficfines.fine.entity.FineStatus;
import com.slpolice.trafficfines.payment.entity.PaymentChannel;
import com.slpolice.trafficfines.payment.entity.PaymentMethod;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin/export")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class ExportController {

    private final AdminService adminService;
    private final List<ExportService> exportServices;

    @GetMapping("/{type}/{format}")
    public ResponseEntity<byte[]> export(
            @PathVariable String type,
            @PathVariable String format,
            @RequestParam(required = false) FineStatus status,
            @RequestParam(required = false) String district,
            @RequestParam(required = false) PaymentMethod method,
            @RequestParam(required = false) PaymentChannel channel) {

        ExportService exporter = exportServices.stream()
                .filter(s -> s.getClass().getSimpleName()
                        .toLowerCase().startsWith(format.toLowerCase()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unsupported format: " + format));

        byte[] data = switch (type + ":" + format) {
            case "fines:csv" -> exporter.exportFinesToCsv(adminService.getAllFines(status, district));
            case "fines:pdf" -> exporter.exportFinesToPdf(adminService.getAllFines(status, district));
            case "payments:csv" -> exporter.exportPaymentsToCsv(adminService.getAllPayments(method, channel));
            case "payments:pdf" -> exporter.exportPaymentsToPdf(adminService.getAllPayments(method, channel));
            case "districts:csv" -> exporter.exportDistrictsToCsv(adminService.getDistrictReports());
            case "districts:pdf" -> exporter.exportDistrictsToPdf(adminService.getDistrictReports());
            case "categories:csv" -> exporter.exportCategoriesToCsv(adminService.getCategoryReports());
            case "categories:pdf" -> exporter.exportCategoriesToPdf(adminService.getCategoryReports());
            case "officers:csv" -> exporter.exportOfficersToCsv(adminService.getOfficerReports());
            case "officers:pdf" -> exporter.exportOfficersToPdf(adminService.getOfficerReports());
            default -> throw new IllegalArgumentException("Unknown export type/format: " + type + "/" + format);
        };

        MediaType mediaType = format.equals("pdf")
                ? MediaType.APPLICATION_PDF
                : new MediaType("text", "csv");

        return ResponseEntity.ok()
                .contentType(mediaType)
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        ContentDisposition.attachment().filename(type + "-report." + format).build().toString())
                .body(data);
    }
}
