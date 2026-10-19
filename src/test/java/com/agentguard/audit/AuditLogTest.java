package com.agentguard.audit;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

public class AuditLogTest {
    AuditEvent event = new AuditEvent(
            "agent-001",
            "documents.read",
            AuditOutcome.SUCCESS,
            Instant.now()
    );
    AuditLog log = new AuditLog();

    @Test
    public void shouldRecordAuditEvent() {
        log.recordEvent(event);
        assertEquals(event, log.getEvents().getFirst());
        assertEquals(1, log.getEvents().size());
    }

    @Test
    public void shouldRejectNullEvent() {
        assertThrows(IllegalArgumentException.class, () -> {
            log.recordEvent(null);
        });
    }

    @Test
    public void shouldReturnImmutableSnapshot() {
        log.recordEvent(event);
        List<AuditEvent> listAE = log.getEvents();
        assertThrows(UnsupportedOperationException.class, () -> {
            listAE.clear();
        });
    }

    @Test
    public void shouldPreserveSnapshotAfterNewEvent() {
        log.recordEvent(event);
        AuditEvent newEvent = new AuditEvent(
                "agent-002",
                "messages.send",
                AuditOutcome.FAILED,
                Instant.now()
        );
        List<AuditEvent> snapshot1 = log.getEvents();
        log.recordEvent(newEvent);
        assertEquals(1, snapshot1.size());
        assertEquals(2, log.getEvents().size());
    }

    @Test
    void shouldRecordConcurrentAuditEvents() throws Exception {
        AuditLog auditLog = new AuditLog();
        AuditEvent event = new AuditEvent(
                "agent-002",
                "documents.read",
                AuditOutcome.SUCCESS,
                Instant.now()
        );
        ExecutorService executor = Executors.newFixedThreadPool(10);
        for (int i = 0; i < 100; i++) {
            executor.execute(() -> {
                auditLog.recordEvent(event);
            });
        }
        executor.shutdown();
        boolean completed = executor.awaitTermination(
                5, TimeUnit.SECONDS
        );
        assertTrue(completed);
        assertEquals(100, auditLog.getEvents().size());

    }

}
