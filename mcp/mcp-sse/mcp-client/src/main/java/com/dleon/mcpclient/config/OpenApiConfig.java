package com.dleon.mcpclient.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

  @Bean
  public OpenAPI customOpenAPI() {
    return new OpenAPI().info(new Info().title("MCP SSE Client API").description(
            "REST API that answers natural-language questions using OpenAI and MCP tools over SSE")
        .version("0.0.1"));
  }
}
