package com.dleon.mcpfilesystemclient.controller;

import com.dleon.mcpfilesystemclient.dto.ChatRequest;
import com.dleon.mcpfilesystemclient.dto.ChatResponse;
import com.dleon.mcpfilesystemclient.service.McpGatewayService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/mcp")
public class McpConsumerController {

    private final McpGatewayService mcpGatewayService;

    public McpConsumerController(McpGatewayService mcpGatewayService) {
        this.mcpGatewayService = mcpGatewayService;
    }

    @PostMapping("/chat")
    public ChatResponse ask(@Valid @RequestBody ChatRequest chatRequest) {
        return new ChatResponse(mcpGatewayService.ask(chatRequest.message()));
    }
}
