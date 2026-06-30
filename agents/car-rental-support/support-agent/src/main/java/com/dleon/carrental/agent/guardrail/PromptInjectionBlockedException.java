package com.dleon.carrental.agent.guardrail;

public class PromptInjectionBlockedException extends RuntimeException {

  public PromptInjectionBlockedException(String message) {
    super(message);
  }
}
