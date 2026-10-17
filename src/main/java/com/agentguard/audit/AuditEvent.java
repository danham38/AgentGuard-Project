package com.agentguard.audit;

import java.time.Instant;

public record AuditEvent(
        String agentId,
        String toolName,
        AuditOutcome outcome,
        Instant timestamp
) {}
