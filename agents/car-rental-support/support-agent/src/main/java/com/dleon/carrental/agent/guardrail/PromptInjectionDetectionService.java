package com.dleon.carrental.agent.guardrail;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.regex.Matcher;

import static com.dleon.carrental.agent.constants.Constants.INJECTION_SCORE_PROMPT_TEMPLATE;
import static com.dleon.carrental.agent.constants.Constants.SCORE_PATTERN;

@Service
public class PromptInjectionDetectionService {

  private final ChatClient detectionClient;

  public PromptInjectionDetectionService(
      @Qualifier("guardrailChatClient") ChatClient detectionClient) {
    this.detectionClient = detectionClient;
  }

  public double scoreInjectionRisk(String userQuery) {
    String raw = detectionClient.prompt()
        .user(spec -> spec.text(INJECTION_SCORE_PROMPT_TEMPLATE).param("user_query", userQuery))
        .call().content();
    return parseScore(raw);
  }

  private static double parseScore(String raw) {
    if (raw == null || raw.isBlank()) {
      return 1.0;
    }
    String trimmed = raw.trim();
    try {
      return Double.parseDouble(trimmed);
    } catch (NumberFormatException ignored) {
      Matcher matcher = SCORE_PATTERN.matcher(trimmed);
      if (matcher.find()) {
        return Double.parseDouble(matcher.group(1));
      }
      return 1.0;
    }
  }
}
