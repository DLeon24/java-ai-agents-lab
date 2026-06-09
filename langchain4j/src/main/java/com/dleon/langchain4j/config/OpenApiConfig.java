package com.dleon.langchain4j.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

  @Bean
  public OpenAPI customOpenAPI() {
    return new OpenAPI().info(new Info().title("LangChain4j Tool Calling").description(
            "REST API that answers natural-language questions using Ollama and LangChain4j @Tool handlers")
        .version("0.0.1"));
  }
}
