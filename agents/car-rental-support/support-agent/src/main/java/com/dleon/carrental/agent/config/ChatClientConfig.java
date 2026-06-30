package com.dleon.carrental.agent.config;

import com.dleon.carrental.agent.tools.BookingTools;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.util.ArrayList;
import java.util.List;

import static com.dleon.carrental.agent.constants.Constants.INPUT_SECURITY_PROMPT;

@Configuration
public class ChatClientConfig {

  @Bean
  @ConditionalOnProperty(name = "app.chat.logging.enabled", havingValue = "true")
  public SimpleLoggerAdvisor simpleLoggerAdvisor() {
    return new SimpleLoggerAdvisor();
  }

  @Bean
  @Primary
  public ChatClient chatClient(ChatModel chatModel, BookingTools bookingTools,
      ToolCallbackProvider mcpTools, QuestionAnswerAdvisor questionAnswerAdvisor,
      ObjectProvider<SimpleLoggerAdvisor> loggerAdvisor) {
    List<Advisor> advisors = new ArrayList<>();
    advisors.add(questionAnswerAdvisor);
    loggerAdvisor.ifAvailable(advisors::add);
    return ChatClient.builder(chatModel).defaultTools(bookingTools).defaultToolCallbacks(mcpTools)
        .defaultAdvisors(advisors).build();
  }

  @Bean
  @Qualifier("guardrailChatClient")
  public ChatClient guardrailChatClient(ChatModel chatModel,
      ObjectProvider<SimpleLoggerAdvisor> loggerAdvisor,
      @Value("${app.guardrail.prompt-injection.temperature:0.0}") double temperature) {
    List<Advisor> advisors = new ArrayList<>();
    loggerAdvisor.ifAvailable(advisors::add);
    ChatClient.Builder builder = ChatClient.builder(chatModel).defaultSystem(INPUT_SECURITY_PROMPT)
        .defaultOptions(OpenAiChatOptions.builder().temperature(temperature).build());
    if (!advisors.isEmpty()) {
      builder.defaultAdvisors(advisors);
    }
    return builder.build();
  }

}
