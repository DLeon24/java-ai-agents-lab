package com.dleon.customersupportagent.guardrail;

public class PromptInjectionBlockedException extends RuntimeException {

  public PromptInjectionBlockedException(String message) {
    super(message);
  }
}
