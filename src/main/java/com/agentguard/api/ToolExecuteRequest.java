package com.agentguard.api;
import java.util.Map;

public record ToolExecuteRequest(String toolName,
                                 Map<String, String> arguments) {
}
