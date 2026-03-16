package com.medico.medico.service.impl;

import com.medico.medico.model.AuditLog;
import com.medico.medico.repository.AuditLogRepository;
import com.medico.medico.service.AuditService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class AuditServiceImpl implements AuditService {

    private final AuditLogRepository auditLogRepository;

    @Override
    public void recordAction(String username, String action, String details) {
        AuditLog log = AuditLog.builder()
                .username(username)
                .action(action)
                .details(details)
                .timestamp(Instant.now())
                .build();
        auditLogRepository.save(log);
    }
}
