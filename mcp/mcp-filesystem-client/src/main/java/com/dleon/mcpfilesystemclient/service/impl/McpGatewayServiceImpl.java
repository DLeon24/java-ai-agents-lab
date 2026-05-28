package com.dleon.mcpfilesystemclient.service.impl;

import com.dleon.mcpfilesystemclient.exception.McpGatewayException;
import com.dleon.mcpfilesystemclient.service.McpGatewayService;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class McpGatewayServiceImpl implements McpGatewayService {

  @Value("${app.filesystem.allowed-dir}")
  private String allowedDir;

  private final ChatClient chatClient;

  public McpGatewayServiceImpl(ChatClient.Builder chatClientBuilder, ToolCallbackProvider tools) {
    this.chatClient = chatClientBuilder
        .defaultToolCallbacks(tools)
        .build();
  }

  @Override
  public String ask(String userMessage) {
    try {
      return callModel(userMessage);
    } catch (Exception exception) {
      throw new McpGatewayException(
          "Unable to process the request at this time. Verify that the MCP provider is active and try again.",
          exception);
    }
  }

  private String callModel(String userMessage) {
    return chatClient.prompt()
        .user(withFilesystemScope(userMessage))
        .call()
        .content();
  }

  private String withFilesystemScope(String userMessage) {
    return "Filesystem scope rule: you can only access files under " + allowedDir + ". "
        + "If the user gives a filename without a path, treat it as " + allowedDir + "/<filename>. "
        + "Never use the current working directory for file operations. User request: " + userMessage;
  }
}
