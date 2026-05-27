package com.dleon.mcpfilesystemclient.controller;

import com.dleon.mcpfilesystemclient.dto.ChatRequest;
import com.dleon.mcpfilesystemclient.dto.ChatResponse;
import com.dleon.mcpfilesystemclient.service.McpGatewayService;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/mcp")
public class McpConsumerController {

    private final McpGatewayService mcpGatewayService;

    public McpConsumerController(McpGatewayService mcpGatewayService) {
        this.mcpGatewayService = mcpGatewayService;
    }

    @PostMapping("/chat")
    public ChatResponse ask(@RequestBody ChatRequest chatRequest) {
        if (chatRequest == null || chatRequest.message() == null || chatRequest.message().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                "The 'message' field is required and must not be blank.");
        }
        return new ChatResponse(mcpGatewayService.ask(chatRequest.message()));
    }
}
