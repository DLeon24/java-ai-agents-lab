package com.dleon.langchain4j.controller;

import com.dleon.langchain4j.dto.ChatRequest;
import com.dleon.langchain4j.dto.ChatResponse;
import com.dleon.langchain4j.service.Assistant;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/tool-calling")
public class ToolCallingController {

  private final Assistant assistant;

  public ToolCallingController(Assistant assistant) {
    this.assistant = assistant;
  }

  @PostMapping("/chat")
  public ChatResponse chat(@RequestBody ChatRequest request) {
    String response = assistant.chat(request.message());
    return new ChatResponse(response);
  }
}
