package com.agentguard.api;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


@RestControllerAdvice(assignableTypes = ToolController.class)
public class ApiExceptionHandler {

    //400 invalid args
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleInvalidArguments(IllegalArgumentException exception) {
        return ResponseEntity.badRequest().body("Invalid request arguments");
    }
    //402 unauthorised
    @ExceptionHandler(SecurityException.class)
    public ResponseEntity<String> handleUnauthorisedExecution(
            SecurityException exception) {

        return ResponseEntity.status(403)
                .body("Tool execution forbidden");
    }
}
