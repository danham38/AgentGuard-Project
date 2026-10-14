package com.agentguard.policy;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class PolicyEngineTest {
    Map<String, Set<String>> config = Map.of(
            "agent-001", Set.of("documents.read"),
            "agent-002", Set.of("documents.read", "messages.send")
    );

    PolicyEngine engine = new PolicyEngine(config);


    @Test
    public void shouldAllowPermittedTool(){
        assertTrue(engine.isAllowed("agent-001", "documents.read"));
    }
    @Test
    public void shouldDenyUnpermittedTool() {
        assertFalse(engine.isAllowed("agent-001", "messages.send"));
    }
    @Test
    public void shouldDenyUnknownAgent() {
        assertFalse(engine.isAllowed("agent-test", "documents.read"));
    }
    @Test
    public void shouldDenyAgentWithNoPermissions() {
        Map<String, Set<String>> map = Map.of(
                "agent-10", Set.of()
        );
        PolicyEngine engine = new PolicyEngine(map);
        assertFalse(engine.isAllowed("agent-10", "documents.read"));
    }
    @Test
    public void shouldDenyInvalidIdentifiersAgentIdNull() {
        assertFalse(engine.isAllowed(null, "documents.read"));
    }
    @Test
    public void shouldDenyInvalidIdentifiersAgentIdBlank() {
        assertFalse(engine.isAllowed("", "documents.read"));
    }
    @Test
    public void shouldDenyInvalidIdentifiersAgentIdBlankWithSpace() {
        assertFalse(engine.isAllowed(" ", "documents.read"));
    }
    @Test
    public void shouldDenyInvalidIdentifiersToolNull() {
        assertFalse(engine.isAllowed("agent-001", null));
    }
    @Test
    public void shouldDenyInvalidIdentifiersToolBlank() {
        assertFalse(engine.isAllowed("agent-001", ""));
    }
    @Test
    public void shouldDenyInvalidIdentifiersToolBlankWithSpace() {
        assertFalse(engine.isAllowed("agent-001", " "));
    }
    @Test
    public void shouldNotReflectExternalPermissionChanges() {
        HashSet<String> mutSet = new HashSet<>(Set.of("documents.read"));
        Map<String, Set<String>> map = new HashMap<>();
        map.put("agent-11", mutSet);
        PolicyEngine engine = new PolicyEngine(map);
        map.put("agent-101", Set.of("tasks.done"));
        assertFalse(engine.isAllowed("agent-101", "tasks.done"));
        mutSet.add("messages.send");
        assertFalse(engine.isAllowed("agent-11", "messages.send"));
        assertTrue(engine.isAllowed("agent-11", "documents.read"));
    }

}
