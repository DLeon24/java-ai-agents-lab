package com.dleon.langchain4j.service;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;

public interface Assistant {

  @SystemMessage("""
      You are a helpful assistant that always responds in English.
      You have access to tools for:
      1. Math operations: add, subtract, multiply, divide, square root, power
      2. Looking up the current date and time
      3. Looking up real country information via an external REST API

      Use the tools when needed to answer accurately.
      For math, ALWAYS use the calculator tools instead of computing the result yourself.
      """)
  String chat(@UserMessage String message);
}
