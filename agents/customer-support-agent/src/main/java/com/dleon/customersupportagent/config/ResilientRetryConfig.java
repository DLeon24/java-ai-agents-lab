package com.dleon.customersupportagent.config;

import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class ResilientRetryConfig {

  @Bean
  public Retry llmChatRetry(@Value("${app.retry.id:llmChat}") String name,
      @Value("${resilience4j.retry.instances.llmChat.max-attempts:4}") int maxAttempts,
      @Value("${resilience4j.retry.instances.llmChat.wait-duration:100ms}") Duration waitDuration) {
    RetryConfig retryConfig =
        RetryConfig.custom().maxAttempts(maxAttempts).waitDuration(waitDuration).build();
    return Retry.of(name, retryConfig);
  }
}
