package com.slpolice.trafficfines.admin.audit;

import org.aspectj.lang.ProceedingJoinPoint;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.lang.reflect.Method;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuditAspect Unit Tests")
class AuditAspectTest {

    @Mock private AuditLogRepository auditLogRepository;
    @Mock private ProceedingJoinPoint joinPoint;

    @Captor
    private ArgumentCaptor<AuditLog> auditLogCaptor;

    private AuditAspect auditAspect;

    @BeforeEach
    void setUp() {
        auditAspect = new AuditAspect(auditLogRepository);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("Should log audit entry when annotated method completes successfully")
    void shouldLogAuditEntry() throws Throwable {
        SecurityContextHolder.getContext().setAuthentication(
                new Authentication() {
                    @Override public String getName() { return "admin1"; }
                    @Override public boolean isAuthenticated() { return true; }
                    @Override public void setAuthenticated(boolean isAuthenticated) {}
                    @Override public java.util.Collection<? extends org.springframework.security.core.GrantedAuthority> getAuthorities() { return java.util.Collections.emptyList(); }
                    @Override public Object getCredentials() { return null; }
                    @Override public Object getDetails() { return null; }
                    @Override public Object getPrincipal() { return null; }
                }
        );

        when(joinPoint.proceed()).thenReturn("some result");
        when(joinPoint.getArgs()).thenReturn(new Object[]{"arg1", 42});

        Method testMethod = TestService.class.getMethod("testAction", String.class);
        Auditable auditable = testMethod.getAnnotation(Auditable.class);

        Object result = auditAspect.audit(joinPoint, auditable);

        assertThat(result).isEqualTo("some result");
        verify(auditLogRepository).save(auditLogCaptor.capture());

        AuditLog logged = auditLogCaptor.getValue();
        assertThat(logged.getAdminUsername()).isEqualTo("admin1");
        assertThat(logged.getAction()).isEqualTo("TEST_ACTION");
        assertThat(logged.getDetails()).isEqualTo("arg1, 42");
        assertThat(logged.getIpAddress()).isEqualTo("unknown");
    }

    @Test
    @DisplayName("Should use anonymous when no authentication is present")
    void shouldUseAnonymousWhenNoAuth() throws Throwable {
        SecurityContextHolder.clearContext();

        when(joinPoint.proceed()).thenReturn("result");
        when(joinPoint.getArgs()).thenReturn(new Object[]{});

        Method testMethod = TestService.class.getMethod("noArgsAction");
        Auditable auditable = testMethod.getAnnotation(Auditable.class);

        auditAspect.audit(joinPoint, auditable);

        verify(auditLogRepository).save(auditLogCaptor.capture());
        assertThat(auditLogCaptor.getValue().getAdminUsername()).isEqualTo("anonymous");
    }

    @Test
    @DisplayName("Should still return the method result when audit persistence fails")
    void shouldNotThrowWhenPersistenceFails() throws Throwable {
        SecurityContextHolder.getContext().setAuthentication(
                new Authentication() {
                    @Override public String getName() { return "admin1"; }
                    @Override public boolean isAuthenticated() { return true; }
                    @Override public void setAuthenticated(boolean isAuthenticated) {}
                    @Override public java.util.Collection<? extends org.springframework.security.core.GrantedAuthority> getAuthorities() { return java.util.Collections.emptyList(); }
                    @Override public Object getCredentials() { return null; }
                    @Override public Object getDetails() { return null; }
                    @Override public Object getPrincipal() { return null; }
                }
        );

        when(joinPoint.proceed()).thenReturn("success");
        when(joinPoint.getArgs()).thenReturn(new Object[]{});
        when(auditLogRepository.save(any())).thenThrow(new RuntimeException("DB error"));

        Method testMethod = TestService.class.getMethod("noArgsAction");
        Auditable auditable = testMethod.getAnnotation(Auditable.class);

        Object result = auditAspect.audit(joinPoint, auditable);

        assertThat(result).isEqualTo("success");
    }

    @SuppressWarnings("unused")
    static class TestService {
        @Auditable(action = "TEST_ACTION")
        public String testAction(String input) {
            return "processed: " + input;
        }

        @Auditable(action = "NO_ARGS_ACTION")
        public String noArgsAction() {
            return "done";
        }
    }
}
