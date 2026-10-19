package com.agentguard.api;


import com.agentguard.executor.ToolExecutor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;

import java.security.Principal;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/tools")
public class ToolController {

    private final ToolExecutor executor;

    public ToolController(ToolExecutor executor) {
        this.executor = executor;
    }

    @PostMapping("/execute")
    public ResponseEntity<String> executeTool(
            @RequestBody ToolExecuteRequest request,
            Principal principal) {
        String toolName = request.toolName();

        Map<String, String> args = request.arguments();
        Optional<String> result = executor.execute(
                principal.getName(),
                request.toolName(),
                request.arguments()
        );
        if (result.isEmpty()) {
           return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(result.orElseThrow());
    }


}
