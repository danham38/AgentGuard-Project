package com.agentguard.executor;

import com.agentguard.audit.AuditEvent;
import com.agentguard.audit.AuditLog;
import com.agentguard.audit.AuditOutcome;
import com.agentguard.policy.PolicyEngine;
import com.agentguard.tool.Tool;
import com.agentguard.tool.ToolRegistry;

import java.time.Instant;
import java.util.Map;
import java.util.Optional;

public class ToolExecutor {
    private final ToolRegistry registry;
    private final PolicyEngine policyEngine;
    private final AuditLog log;

    public ToolExecutor(ToolRegistry registry,
                        PolicyEngine policyEngine,
                        AuditLog log) {
        if (registry == null || policyEngine == null || log == null) {
            throw new IllegalArgumentException("ToolRegistry, PolicyEngine and AuditLog must not be null");
        }
        this.registry = registry;
        this.policyEngine = policyEngine;
        this.log = log;
    }

    public Optional<String> execute(
            String agentId,
            String toolName,
            Map<String, String> args) {
            boolean isAllowed = policyEngine.isAllowed(agentId, toolName);
            if (!isAllowed) {
                //creating the audit event before the ex is thrown
                AuditEvent event = new AuditEvent(
                        agentId,
                        toolName,
                        AuditOutcome.DENIED,
                        Instant.now()
                );
                log.recordEvent(event);
                throw new SecurityException("Not authorised");
            }
           // Optional<Tool> tool = registry.findTool(toolName);
            Optional<String> result;
            try {
                Tool tool = registry.findTool(toolName).orElseThrow(() ->
                        new IllegalArgumentException("Tool not found"));
                result = tool.execute(args);

            } catch (RuntimeException e) {
                AuditEvent event = new AuditEvent(
                        agentId,
                        toolName,
                        AuditOutcome.FAILED,
                        Instant.now()
                );
                log.recordEvent(event);
                throw e;
            }


            AuditEvent event = new AuditEvent(
                    agentId,
                    toolName,
                    AuditOutcome.SUCCESS,
                    Instant.now()
            );
            log.recordEvent(event);
            return result;

    }

}
