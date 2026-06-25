package com.dleon.customersupportagent.agent;

import com.dleon.customersupportagent.guardrail.PromptInjectionGuard;
import com.dleon.customersupportagent.rag.RagRetriever;
import com.dleon.customersupportagent.resilience.ResilientLlmInvoker;
import com.dleon.customersupportagent.tools.BookingTools;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

import static com.dleon.customersupportagent.constants.Constants.CAR_SUPPORT_SYSTEM_PROMPT_TEMPLATE;

@Service
public class CustomerSupportAgent {

  private final ChatClient chatClient;
  private final ChatMemory chatMemory;
  private final RagRetriever ragRetriever;
  private final BookingTools bookingTools;
  private final ToolCallbackProvider mcpTools;
  private final PromptInjectionGuard promptInjectionGuard;
  private final ResilientLlmInvoker resilientLlmInvoker;

  public CustomerSupportAgent(ChatModel chatModel, ChatMemory chatMemory, RagRetriever ragRetriever,
      BookingTools bookingTools, ToolCallbackProvider mcpTools,
      PromptInjectionGuard promptInjectionGuard, ResilientLlmInvoker resilientLlmInvoker) {
    this.chatMemory = chatMemory;
    this.ragRetriever = ragRetriever;
    this.bookingTools = bookingTools;
    this.mcpTools = mcpTools;
    this.promptInjectionGuard = promptInjectionGuard;
    this.resilientLlmInvoker = resilientLlmInvoker;
    this.chatClient = ChatClient.builder(chatModel).defaultAdvisors(new SimpleLoggerAdvisor()).build();
  }

  public String chat(String sessionId, String userMessage) {
    return resilientLlmInvoker.invoke(() -> {
      promptInjectionGuard.validate(userMessage);
      return invokeLlm(sessionId, userMessage);
    });
  }

  private String invokeLlm(String sessionId, String userMessage) {
    String augmentedMessage = ragRetriever.augmentUserMessage(userMessage);
    return chatClient.prompt().system(CAR_SUPPORT_SYSTEM_PROMPT_TEMPLATE.formatted(LocalDate.now()))
        .advisors(MessageChatMemoryAdvisor.builder(chatMemory).conversationId(sessionId).build())
        .tools(bookingTools).toolCallbacks(mcpTools).user(augmentedMessage).call().content();
  }
}
