package com.dleon.multimodal.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

  @Bean
  public OpenAPI customOpenAPI() {
    return new OpenAPI().info(new Info().title("Multimodal API").description(
            "REST API for multimodal AI capabilities: image generation, vision analysis, and voice processing")
        .version("1.0.0"));
  }
}
