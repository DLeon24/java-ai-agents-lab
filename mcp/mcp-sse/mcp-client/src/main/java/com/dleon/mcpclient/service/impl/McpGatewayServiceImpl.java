package com.dleon.mcpclient.service.impl;

import com.dleon.mcpclient.exception.McpGatewayException;
import com.dleon.mcpclient.service.McpGatewayService;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.stereotype.Service;

@Service
public class McpGatewayServiceImpl implements McpGatewayService {

  private final ChatClient chatClient;

  public McpGatewayServiceImpl(ChatClient.Builder chatClientBuilder, ToolCallbackProvider tools) {
    this.chatClient = chatClientBuilder.defaultToolCallbacks(tools).build();
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
    return chatClient.prompt().system(
            "You are a helpful assistant, able to answer questions about board games,\n" + "including how many players can play and how long a game typically takes\n" + "to play.")
        .user(userMessage).call().content();
  }
}
