package com.agentguard.executor;

import com.agentguard.audit.AuditEvent;
import com.agentguard.audit.AuditLog;
import com.agentguard.audit.AuditOutcome;
import com.agentguard.policy.PolicyEngine;
import com.agentguard.tool.DocumentReadTool;
import com.agentguard.tool.Tool;
import com.agentguard.tool.ToolRegistry;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class ToolExecutorTest {
    public static class TestTool implements Tool {
        private int exeCount =0;
        private final String name;
        TestTool(String name) {
            this.name = name;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public Optional<String> execute(Map<String, String> args) {
            exeCount++;
            throw new UnsupportedOperationException("Execution unsupported in registry tests");
        }
    }



    Map<String, String> documentsMap = Map.of(
            "DOC-001", "Security policy"
    );
    Map<String, Set<String>> permissions = Map.of(
            "agent-001", Set.of("documents.read")
    );
    Map<String, String> args = Map.of(
            "documentId", "DOC-001"
    );

    AuditEvent event = new AuditEvent(
            "agent-001",
            "messages.send",
            AuditOutcome.DENIED,
            Instant.now()
    );
    AuditLog log = new AuditLog();


    @Test
    public void shouldExecuteAuthorisedTool() {

       DocumentReadTool docReadTool = new DocumentReadTool(documentsMap);
       ToolRegistry registry = new ToolRegistry();
       PolicyEngine policyEngine = new PolicyEngine(permissions);
       registry.register(docReadTool);

       ToolExecutor exe = new ToolExecutor(registry, policyEngine, log);
       Optional<String> result = exe.execute("agent-001", "documents.read", args);
       assertEquals("Security policy", result.orElseThrow());

    }
    @Test
    public void shouldRejectUnauthorisedTool() {
        DocumentReadTool docReadTool = new DocumentReadTool(documentsMap);
        ToolRegistry registry = new ToolRegistry();
        PolicyEngine policyEngine = new PolicyEngine(permissions);
        registry.register(docReadTool);

        ToolExecutor exe = new ToolExecutor(registry, policyEngine, log);
        assertThrows(SecurityException.class, () -> {
            exe.execute("agent-010", "documents.read", args);
        });
    }

    @Test
    public void shouldRejectToolIfNoPermissions() {
        TestTool tool = new TestTool("messages.send");
        //DocumentReadTool docReadTool = new DocumentReadTool(documentsMap);
        ToolRegistry registry = new ToolRegistry();
        PolicyEngine policyEngine = new PolicyEngine(permissions);
        registry.register(tool);
        ToolExecutor exe = new ToolExecutor(registry, policyEngine, log);
        //Optional<String> result = exe.execute("agent-001", "documents.read", args);
        assertThrows(SecurityException.class, () -> {
            exe.execute("agent-001", "messages.send", args);
        });

    }

    @Test
    public void shouldNotRunExecuteMethodIfPermissionDenied() {
        TestTool tool = new TestTool("messages.send");
        //DocumentReadTool docReadTool = new DocumentReadTool(documentsMap);
        ToolRegistry registry = new ToolRegistry();
        PolicyEngine policyEngine = new PolicyEngine(permissions);
        registry.register(tool);
        ToolExecutor exe = new ToolExecutor(registry, policyEngine, log);
        //Optional<String> result = exe.execute("agent-001", "documents.read", args);
        assertThrows(SecurityException.class, () -> {
            exe.execute("agent-001", "messages.send", args);
        });
        assertEquals(0, tool.exeCount);
    }

    @Test
    public void shouldRejectUnknownTool() {
        ToolRegistry registry = new ToolRegistry();
        PolicyEngine policyEngine = new PolicyEngine(permissions);
        ToolExecutor exe = new ToolExecutor(registry, policyEngine, log);
        assertThrows(IllegalArgumentException.class, () -> {
            exe.execute("agent-001", "documents.read", args);
        });
    }

    @Test
    public void shouldReturnEmptyForUnknownDocument() {
        Map<String, String> argsUnknownDoc = Map.of(
                "documentId", "DOC-002"
        );
        DocumentReadTool docReadTool = new DocumentReadTool(documentsMap);
        ToolRegistry registry = new ToolRegistry();
        PolicyEngine policyEngine = new PolicyEngine(permissions);
        registry.register(docReadTool);

        ToolExecutor exe = new ToolExecutor(registry, policyEngine, log);
        Optional<String> result = exe.execute("agent-001", "documents.read", argsUnknownDoc);
        assertTrue(result.isEmpty());
    }

    @Test
    public void shouldAuditDeniedExecution() {
        ToolRegistry registry = new ToolRegistry();
        TestTool tool = new TestTool("documents.read");
        registry.register(tool);
        PolicyEngine policyEngine = new PolicyEngine(permissions);
        ToolExecutor exe = new ToolExecutor(registry, policyEngine, log);
        assertThrows(SecurityException.class, () -> {
            exe.execute("agent-001", "messages.send", args);
        });
        List<AuditEvent> events = log.getEvents();
        assertEquals(1, events.size());
        assertEquals("agent-001", events.getFirst().agentId());
        assertEquals("messages.send", events.getFirst().toolName());
        assertEquals(AuditOutcome.DENIED, events.getFirst().outcome());
        assertNotNull(events.getFirst().timestamp());
    }

    @Test
    public void shouldAuditSuccessfulExecution() {
        DocumentReadTool docReadTool = new DocumentReadTool(documentsMap);
        ToolRegistry registry = new ToolRegistry();
        registry.register(docReadTool);
        PolicyEngine policyEngine = new PolicyEngine(permissions);
        ToolExecutor exe = new ToolExecutor(registry, policyEngine, log);
        Optional<String> result = exe.execute("agent-001", "documents.read", args);
        List<AuditEvent> events = log.getEvents();
        assertEquals("Security policy", result.orElseThrow());
        assertEquals(1, events.size());
        assertEquals("agent-001", events.getFirst().agentId());
        assertEquals("documents.read", events.getFirst().toolName());
        assertEquals(AuditOutcome.SUCCESS, events.getFirst().outcome());
        assertNotNull(events.getFirst().timestamp());
    }

    @Test
    public void shouldAuditFailedExecutionForUnknownTool() {
        DocumentReadTool docReadTool = new DocumentReadTool(documentsMap);
        ToolRegistry registry = new ToolRegistry();
        //registry.register(docReadTool);
        PolicyEngine policyEngine = new PolicyEngine(permissions);
        ToolExecutor exe = new ToolExecutor(registry, policyEngine, log);
        //Optional<String> result = exe.execute("agent-001", "documents.read", args);

        assertThrows(IllegalArgumentException.class, () -> {
            exe.execute("agent-001", "documents.read", args);
        });
        List<AuditEvent> events = log.getEvents();
        assertEquals(1, log.getEvents().size());
        assertEquals(AuditOutcome.FAILED, log.getEvents().getFirst().outcome());
        assertEquals(1, events.size());
        assertEquals("agent-001", events.getFirst().agentId());
        assertEquals("documents.read", events.getFirst().toolName());
        assertEquals(AuditOutcome.FAILED, events.getFirst().outcome());
        assertNotNull(events.getFirst().timestamp());
    }

    @Test
    public void shouldAuditFailedExecutionForInvalidArguments() {
        Map<String, String> invalidArgs = Map.of(
                "documentId", " "
        );
        DocumentReadTool docReadTool = new DocumentReadTool(documentsMap);
        ToolRegistry registry = new ToolRegistry();
        registry.register(docReadTool);
        PolicyEngine policyEngine = new PolicyEngine(permissions);
        ToolExecutor exe = new ToolExecutor(registry, policyEngine, log);
        //Optional<String> result = exe.execute("agent-001", "documents.read", args);

        assertThrows(IllegalArgumentException.class, () -> {
            exe.execute("agent-001", "documents.read", invalidArgs);
        });
        List<AuditEvent> events = log.getEvents();
        assertEquals(1, log.getEvents().size());
        assertEquals(AuditOutcome.FAILED, log.getEvents().getFirst().outcome());
        assertEquals("agent-001", events.getFirst().agentId());
        assertEquals("documents.read", events.getFirst().toolName());
        assertEquals(AuditOutcome.FAILED, events.getFirst().outcome());
        assertNotNull(events.getFirst().timestamp());
    }

    @Test
    public void shouldAuditSuccessWhenEmptyResult() {
        Map<String, String> map = Map.of(
                "documentId", "DOC-999"
        );
        DocumentReadTool docReadTool = new DocumentReadTool(documentsMap);
        ToolRegistry registry = new ToolRegistry();
        registry.register(docReadTool);
        PolicyEngine policyEngine = new PolicyEngine(permissions);
        ToolExecutor exe = new ToolExecutor(registry, policyEngine, log);
        //Optional<String> result = exe.execute("agent-001", "documents.read", args);


        exe.execute("agent-001", "documents.read", map);
        List<AuditEvent> events = log.getEvents();
        assertEquals(1, log.getEvents().size());
        assertEquals(AuditOutcome.SUCCESS, log.getEvents().getFirst().outcome());
        assertEquals("agent-001", events.getFirst().agentId());
        assertEquals("documents.read", events.getFirst().toolName());
        assertEquals(AuditOutcome.SUCCESS, events.getFirst().outcome());
        assertNotNull(events.getFirst().timestamp());
    }


}
