package com.agentguard.executor;

import com.agentguard.policy.PolicyEngine;
import com.agentguard.tool.DocumentReadTool;
import com.agentguard.tool.Tool;
import com.agentguard.tool.ToolRegistry;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

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
    @Test
    public void shouldExecuteAuthorisedTool() {

       DocumentReadTool docReadTool = new DocumentReadTool(documentsMap);
       ToolRegistry registry = new ToolRegistry();
       PolicyEngine policyEngine = new PolicyEngine(permissions);
       registry.register(docReadTool);

       ToolExecutor exe = new ToolExecutor(registry, policyEngine);
       Optional<String> result = exe.execute("agent-001", "documents.read", args);
       assertEquals("Security policy", result.orElseThrow());

    }
    @Test
    public void shouldRejectUnauthorisedTool() {
        DocumentReadTool docReadTool = new DocumentReadTool(documentsMap);
        ToolRegistry registry = new ToolRegistry();
        PolicyEngine policyEngine = new PolicyEngine(permissions);
        registry.register(docReadTool);

        ToolExecutor exe = new ToolExecutor(registry, policyEngine);
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
        ToolExecutor exe = new ToolExecutor(registry, policyEngine);
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
        ToolExecutor exe = new ToolExecutor(registry, policyEngine);
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
        ToolExecutor exe = new ToolExecutor(registry, policyEngine);
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

        ToolExecutor exe = new ToolExecutor(registry, policyEngine);
        Optional<String> result = exe.execute("agent-001", "documents.read", argsUnknownDoc);
        assertTrue(result.isEmpty());
    }

}
