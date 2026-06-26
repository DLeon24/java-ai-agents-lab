package com.dleon.customersupportagent.agent;

import com.dleon.customersupportagent.guardrail.PromptInjectionGuard;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.time.LocalDate;

import static com.dleon.customersupportagent.constants.Constants.CAR_SUPPORT_SYSTEM_PROMPT_TEMPLATE;

@Service
public class CustomerSupportAgent {

  private final ChatClient chatClient;
  private final ChatMemory chatMemory;
  private final PromptInjectionGuard promptInjectionGuard;

  public CustomerSupportAgent(ChatClient chatClient, ChatMemory chatMemory,
      PromptInjectionGuard promptInjectionGuard) {
    this.chatClient = chatClient;
    this.chatMemory = chatMemory;
    this.promptInjectionGuard = promptInjectionGuard;
  }

  public Flux<String> chatStream(String sessionId, String userMessage) {
    promptInjectionGuard.validate(userMessage);
    return chatClient.prompt().system(spec -> spec.text(CAR_SUPPORT_SYSTEM_PROMPT_TEMPLATE)
            .param("current_date", LocalDate.now()))
        .advisors(MessageChatMemoryAdvisor.builder(chatMemory).conversationId(sessionId).build())
        .user(userMessage).stream().content();
  }
}
