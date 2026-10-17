package com.agentguard.executor;

import com.agentguard.policy.PolicyEngine;
import com.agentguard.tool.Tool;
import com.agentguard.tool.ToolRegistry;

import java.util.Map;
import java.util.Optional;

public class ToolExecutor {
    private final ToolRegistry registry;
    private final PolicyEngine policyEngine;

    public ToolExecutor(ToolRegistry registry,
                        PolicyEngine policyEngine) {
        if (registry == null || policyEngine == null) {
            throw new IllegalArgumentException("ToolRegistry and PolicyEngine must not be null");
        }
        this.registry = registry;
        this.policyEngine = policyEngine;
    }

    public Optional<String> execute(
            String agentId,
            String toolName,
            Map<String, String> args) {
            boolean isAllowed = policyEngine.isAllowed(agentId, toolName);
            if (!isAllowed) {
                throw new SecurityException("Not authorised");
            }
           // Optional<Tool> tool = registry.findTool(toolName);

            Tool tool = registry.findTool(toolName).orElseThrow(() ->
                 new IllegalArgumentException("Tool not found"));

            return tool.execute(args);

    }

}
