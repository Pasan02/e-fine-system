package com.slpolice.trafficfines.admin.audit;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Arrays;
import java.util.stream.Collectors;

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class AuditAspect {

    private final AuditLogRepository auditLogRepository;

    @Around("@annotation(auditable)")
    public Object audit(ProceedingJoinPoint joinPoint, Auditable auditable) throws Throwable {
        String action = auditable.action();
        String details = buildDetails(joinPoint);

        Object result = joinPoint.proceed();

        try {
            String username = resolveUsername();
            String ipAddress = resolveIpAddress();
            AuditLog auditLog = AuditLog.builder()
                    .adminUsername(username)
                    .action(action)
                    .details(details)
                    .ipAddress(ipAddress)
                    .build();
            auditLogRepository.save(auditLog);
            log.info("Audit [{}] by {} from IP {}: {}", action, username, ipAddress, details);
        } catch (Exception e) {
            log.warn("Failed to persist audit log for action {}: {}", action, e.getMessage());
        }

        return result;
    }

    private String buildDetails(ProceedingJoinPoint joinPoint) {
        String args = Arrays.stream(joinPoint.getArgs())
                .map(a -> a != null ? a.toString() : "null")
                .collect(Collectors.joining(", "));
        if (args.isEmpty()) return "";
        return args;
    }

    private String resolveUsername() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated()) {
            return auth.getName();
        }
        return "anonymous";
    }

    private String resolveIpAddress() {
        ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attrs != null) {
            HttpServletRequest request = attrs.getRequest();
            String ip = request.getHeader("X-Forwarded-For");
            if (ip != null && !ip.isEmpty()) {
                return ip.split(",")[0].trim();
            }
            return request.getRemoteAddr();
        }
        return "unknown";
    }
}
