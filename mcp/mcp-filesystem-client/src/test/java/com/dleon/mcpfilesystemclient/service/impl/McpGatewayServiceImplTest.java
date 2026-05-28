package com.dleon.mcpfilesystemclient.service.impl;

import com.dleon.mcpfilesystemclient.exception.McpGatewayException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class McpGatewayServiceImplTest {

  private ChatClient.Builder chatClientBuilder;
  private ChatClient chatClient;
  private McpGatewayServiceImpl service;

  @BeforeEach
  void setUp() {
    chatClientBuilder = mock(ChatClient.Builder.class);
    chatClient = mock(ChatClient.class, RETURNS_DEEP_STUBS);
    ToolCallbackProvider tools = mock(ToolCallbackProvider.class);

    when(chatClientBuilder.defaultToolCallbacks(tools)).thenReturn(chatClientBuilder);
    when(chatClientBuilder.build()).thenReturn(chatClient);

    service = new McpGatewayServiceImpl(chatClientBuilder, tools);
    ReflectionTestUtils.setField(service, "allowedDir", "/Users/test/personal-docs");
  }

  @Test
  void askReturnsModelResponse() {
    when(chatClient.prompt().user(anyString()).call().content()).thenReturn("model-response");

    String response = service.ask("list files");

    assertEquals("model-response", response);
    verify(chatClient.prompt()).user(argThat((String prompt) ->
        prompt.contains("/Users/test/personal-docs") && prompt.contains("list files")));
  }

  @Test
  void askWrapsUnexpectedErrorsInMcpGatewayException() {
    when(chatClient.prompt().user(anyString()).call().content())
        .thenThrow(new RuntimeException("boom"));

    McpGatewayException ex = assertThrows(McpGatewayException.class, () -> service.ask("list files"));

    assertEquals("Unable to process the request at this time. Verify that the MCP provider is active and try again.",
        ex.getMessage());
  }
}
