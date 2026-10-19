package com.agentguard.api;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class HealthController {

    //maps http GET requests at /api/health to this method
    @GetMapping("/api/health")
    public String health() {
        return "AgentGuard running";
    }
}
