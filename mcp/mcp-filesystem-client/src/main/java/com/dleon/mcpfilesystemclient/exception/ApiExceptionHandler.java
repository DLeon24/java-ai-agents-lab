package com.dleon.mcpfilesystemclient.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class ApiExceptionHandler {

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<Map<String, String>> handleInvalidRequest(MethodArgumentNotValidException ex) {
    String message = ex.getBindingResult().getFieldError() != null
        ? ex.getBindingResult().getFieldError().getDefaultMessage()
        : "Invalid request";
    return ResponseEntity.badRequest().body(Map.of("error", message));
  }

  @ExceptionHandler(McpGatewayException.class)
  public ResponseEntity<Map<String, String>> handleGatewayFailure(McpGatewayException ex) {
    return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(Map.of("error", ex.getMessage()));
  }
}
