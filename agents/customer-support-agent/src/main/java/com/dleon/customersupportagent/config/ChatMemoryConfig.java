package com.dleon.customersupportagent.config;

import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.memory.repository.jdbc.JdbcChatMemoryRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ChatMemoryConfig {

  @Bean
  public ChatMemory chatMemory(JdbcChatMemoryRepository repository,
      @Value("${app.chat.memory.max-messages}") int maxMessages) {
    return MessageWindowChatMemory.builder().chatMemoryRepository(repository)
        .maxMessages(maxMessages).build();
  }
}
