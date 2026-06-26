package com.dleon.carrental.agent.guardrail;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class PromptInjectionGuard {

  private final PromptInjectionDetectionService detectionService;
  private final double threshold;

  public PromptInjectionGuard(PromptInjectionDetectionService detectionService,
      @Value("${app.guardrail.prompt-injection.threshold}") double threshold) {
    this.detectionService = detectionService;
    this.threshold = threshold;
  }

  public void validate(String userMessage) {
    double score = detectionService.scoreInjectionRisk(userMessage);
    if (score > threshold) {
      throw new PromptInjectionBlockedException("Prompt injection detected");
    }
  }
}
