package com.slpolice.trafficfines.shared.util;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Generates unique fine reference numbers in the format: TF-YYYY-DISTRICT-NNNNN
 * Example: TF-2026-WP-00001
 *
 * As specified in the implementation plan Key Design Decisions:
 * "reference_number: Unique alphanumeric code printed on the physical fine sheet (e.g., TF-2026-WP-00001)"
 *
 * Note: In production this counter should be replaced with a DB sequence to handle
 * concurrent requests safely. For this project scope, AtomicLong is sufficient.
 */
@Component
public class ReferenceNumberGenerator {

    private static final String PREFIX = "TF";
    private final AtomicLong counter = new AtomicLong(1);

    /**
     * Generates a new unique reference number for a fine.
     *
     * @param districtCode Short district code (e.g., "WP" for Western Province)
     * @return A reference number string, e.g., "TF-2026-WP-00001"
     */
    public String generate(String districtCode) {
        int year = LocalDateTime.now().getYear();
        long sequence = counter.getAndIncrement();
        return String.format("%s-%d-%s-%05d", PREFIX, year, districtCode.toUpperCase(), sequence);
    }
}
