package com.slpolice.trafficfines.admin.audit;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("AuditLog Entity Unit Tests")
class AuditLogTest {

    @Test
    @DisplayName("Should build and read AuditLog with all fields")
    void shouldBuildAuditLog() {
        AuditLog log = AuditLog.builder()
                .adminUsername("admin1")
                .action("VIEW_DASHBOARD")
                .details("no filters")
                .ipAddress("192.168.1.1")
                .build();

        assertThat(log.getAdminUsername()).isEqualTo("admin1");
        assertThat(log.getAction()).isEqualTo("VIEW_DASHBOARD");
        assertThat(log.getDetails()).isEqualTo("no filters");
        assertThat(log.getIpAddress()).isEqualTo("192.168.1.1");
        assertThat(log.getId()).isNull();
    }

    @Test
    @DisplayName("Should handle null details and ipAddress")
    void shouldHandleNullFields() {
        AuditLog log = AuditLog.builder()
                .adminUsername("admin1")
                .action("VIEW_FINES")
                .build();

        assertThat(log.getAdminUsername()).isEqualTo("admin1");
        assertThat(log.getAction()).isEqualTo("VIEW_FINES");
        assertThat(log.getDetails()).isNull();
        assertThat(log.getIpAddress()).isNull();
    }
}
