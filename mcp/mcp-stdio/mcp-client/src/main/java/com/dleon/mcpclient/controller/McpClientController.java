package com.dleon.mcpclient.controller;

import com.dleon.mcpclient.dto.ChatRequest;
import com.dleon.mcpclient.dto.ChatResponse;
import com.dleon.mcpclient.service.McpGatewayService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/mcp")
public class McpClientController {

  private final McpGatewayService mcpGatewayService;

  public McpClientController(McpGatewayService mcpGatewayService) {
    this.mcpGatewayService = mcpGatewayService;
  }

  @PostMapping("/chat")
  public ChatResponse ask(@Valid @RequestBody ChatRequest chatRequest) {
    return new ChatResponse(mcpGatewayService.ask(chatRequest.message()));
  }
}

