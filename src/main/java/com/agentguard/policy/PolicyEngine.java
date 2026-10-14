package com.agentguard.policy;

import com.agentguard.tool.Tool;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class PolicyEngine {
    private final Map<String, Set<String>> permissions;

    public PolicyEngine(Map<String, Set<String>> permissions) {
        Map<String, Set<String>> snapshot = new HashMap<>();
        for (Map.Entry<String, Set<String>> entry : permissions.entrySet()) {
            // Get the agent ID
            String id = entry.getKey();
            // Get the agent's permissions
            Set<String> pms = entry.getValue();
            // Copy that Set
            Set<String> immutableSet = Set.copyOf(pms);
            // Add it to snapshot
            snapshot.put(id, immutableSet);

        }
        this.permissions = Map.copyOf(snapshot);
    }

    public boolean isAllowed(String agentId, String toolName) {
        if (agentId == null || agentId.isBlank() || toolName == null || toolName.isBlank()) {
            return false;
        }
        return permissions.containsKey(agentId) && permissions.get(agentId).contains(toolName);
    }

}
