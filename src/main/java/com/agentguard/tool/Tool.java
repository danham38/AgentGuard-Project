package com.agentguard.tool;


import java.util.Map;
import java.util.Optional;

public interface Tool {
    String getName();
    Optional<String> execute(Map<String, String> args);
}
