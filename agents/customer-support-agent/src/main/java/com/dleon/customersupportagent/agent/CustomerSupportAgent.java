package com.dleon.customersupportagent.agent;

import com.dleon.customersupportagent.guardrail.PromptInjectionGuard;
import com.dleon.customersupportagent.resilience.ResilientLlmInvoker;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

import static com.dleon.customersupportagent.constants.Constants.CAR_SUPPORT_SYSTEM_PROMPT_TEMPLATE;

@Service
public class CustomerSupportAgent {

  private final ChatClient chatClient;
  private final ChatMemory chatMemory;
  private final PromptInjectionGuard promptInjectionGuard;
  private final ResilientLlmInvoker resilientLlmInvoker;

  public CustomerSupportAgent(ChatClient chatClient, ChatMemory chatMemory,
      PromptInjectionGuard promptInjectionGuard, ResilientLlmInvoker resilientLlmInvoker) {
    this.chatClient = chatClient;
    this.chatMemory = chatMemory;
    this.promptInjectionGuard = promptInjectionGuard;
    this.resilientLlmInvoker = resilientLlmInvoker;
  }

  public String chat(String sessionId, String userMessage) {
    promptInjectionGuard.validate(userMessage);
    return resilientLlmInvoker.invoke(() -> invokeLlm(sessionId, userMessage));
  }

  private String invokeLlm(String sessionId, String userMessage) {
    return chatClient.prompt().system(spec -> spec.text(CAR_SUPPORT_SYSTEM_PROMPT_TEMPLATE)
            .param("current_date", LocalDate.now()))
        .advisors(MessageChatMemoryAdvisor.builder(chatMemory).conversationId(sessionId).build())
        .user(userMessage).call().content();
  }
}
