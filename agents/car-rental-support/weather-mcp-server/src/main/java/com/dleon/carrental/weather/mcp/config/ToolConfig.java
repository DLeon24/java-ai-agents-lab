package com.dleon.carrental.weather.mcp.config;

import com.dleon.carrental.weather.mcp.tools.WeatherTools;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ToolConfig {

  @Bean
  ToolCallbackProvider weatherToolCallbacks(WeatherTools weatherTools) {
    return MethodToolCallbackProvider.builder().toolObjects(weatherTools).build();
  }
}
