package com.agentguard.audit;

import java.util.ArrayList;
import java.util.List;

public class AuditLog {
    private final List<AuditEvent> events = new ArrayList<>();

    public void recordEvent(AuditEvent event) {
        if (event == null) {
            throw new IllegalArgumentException("Audit event cannot be null");
        }
        events.add(event);
    }


    public List<AuditEvent> getEvents() {
        List<AuditEvent> snapshot = List.copyOf(events);
        return snapshot;
    }


}
