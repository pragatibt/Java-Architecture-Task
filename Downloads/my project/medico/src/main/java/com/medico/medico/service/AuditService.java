package com.medico.medico.service;

public interface AuditService {
    void recordAction(String username, String action, String details);
}
