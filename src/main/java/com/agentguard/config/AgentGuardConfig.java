package com.agentguard.config;
import com.agentguard.audit.AuditLog;
import com.agentguard.executor.ToolExecutor;
import com.agentguard.policy.PolicyEngine;
import com.agentguard.tool.DocumentReadTool;
import com.agentguard.tool.MessageSendTool;
import com.agentguard.tool.ToolRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Configuration
public class AgentGuardConfig {

    //create tool registry that registers tools
    @Bean
    public ToolRegistry toolRegistry() {
        ToolRegistry registry = new ToolRegistry();
        Map<String, String> docMap = Map.of(
                "DOC-001", "Security policy"
        );
        DocumentReadTool docReadTool = new DocumentReadTool(docMap);
        MessageSendTool messageSendTool = new MessageSendTool();

        registry.register(docReadTool);
        registry.register(messageSendTool);

        return registry;
    }

    @Bean
    public PolicyEngine policyEngine() {
        Map<String, Set<String>> permissions = Map.of(
                "agent-001", Set.of("documents.read")
        );
        return new PolicyEngine(permissions);
    }

    @Bean
    public AuditLog auditLog() {
        return new AuditLog();
    }

    @Bean
    public ToolExecutor toolExecutor(
            ToolRegistry registry,
            PolicyEngine policyEngine,
            AuditLog log) {
        return new ToolExecutor(registry, policyEngine, log);

    }

}
