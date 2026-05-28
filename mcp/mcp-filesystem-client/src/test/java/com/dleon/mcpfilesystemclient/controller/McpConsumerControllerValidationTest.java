package com.dleon.mcpfilesystemclient.controller;

import com.dleon.mcpfilesystemclient.exception.McpGatewayException;
import com.dleon.mcpfilesystemclient.service.McpGatewayService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(McpConsumerController.class)
class McpConsumerControllerValidationTest {

  @Autowired
  private MockMvc mockMvc;

  @MockitoBean
  private McpGatewayService mcpGatewayService;

  @Test
  void chatRejectsBlankMessage() throws Exception {
    mockMvc.perform(post("/api/mcp/chat")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"message\":\"   \"}"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.error").value("message must not be blank"));
  }

  @Test
  void chatRejectsMissingMessage() throws Exception {
    mockMvc.perform(post("/api/mcp/chat")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{}"))
        .andExpect(status().isBadRequest());
  }

  @Test
  void chatReturnsBadGatewayWhenServiceFails() throws Exception {
    when(mcpGatewayService.ask("hello")).thenThrow(
        new McpGatewayException("Unable to process the request at this time. Verify that the MCP provider is active and try again.", null));

    mockMvc.perform(post("/api/mcp/chat")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"message\":\"hello\"}"))
        .andExpect(status().isBadGateway())
        .andExpect(jsonPath("$.error").value(
            "Unable to process the request at this time. Verify that the MCP provider is active and try again."));
  }
}
