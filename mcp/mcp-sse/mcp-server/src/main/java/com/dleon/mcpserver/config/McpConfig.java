package com.dleon.mcpserver.config;

import com.dleon.mcpserver.prompt.PromptProvider;
import com.dleon.mcpserver.tools.GameTools;
import com.dleon.mcpserver.rag.ResourceProvider;
import com.logaritex.mcp.spring.SpringAiMcpAnnotationProvider;
import io.modelcontextprotocol.server.McpServerFeatures;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class McpConfig {

  @Bean
  ToolCallbackProvider toolCallbackProvider(GameTools tools) {
    return MethodToolCallbackProvider.builder().toolObjects(tools).build();
  }

  @Bean
  List<McpServerFeatures.SyncPromptSpecification> myPrompts(PromptProvider promptProvider) {
    return SpringAiMcpAnnotationProvider.createSyncPromptSpecifications(List.of(promptProvider));
  }

  @Bean
  public List<McpServerFeatures.SyncResourceSpecification> myResources(
      ResourceProvider resourceProvider) {
    return SpringAiMcpAnnotationProvider.createSyncResourceSpecifications(
        List.of(resourceProvider));
  }
}
