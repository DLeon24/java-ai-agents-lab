package com.dleon.customersupportagent.websocket;

import com.dleon.customersupportagent.agent.CustomerSupportAgent;
import com.dleon.customersupportagent.guardrail.PromptInjectionBlockedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.io.UncheckedIOException;

@Component
public class CustomerSupportAgentWebSocketHandler extends TextWebSocketHandler {

  private static final Logger log = LoggerFactory.getLogger(CustomerSupportAgentWebSocketHandler.class);
  private static final String WELCOME = "Welcome to Car Rental! How can I help you today?";
  private static final String GUARDRAIL_FAILURE = """
      Sorry, I am unable to process your request at the moment. It's not something I'm allowed to do.""";
  private static final String GENERIC_FAILURE = "I ran into some problems. Please try again.";

  private final CustomerSupportAgent customerSupportAgent;

  public CustomerSupportAgentWebSocketHandler(CustomerSupportAgent customerSupportAgent) {
    this.customerSupportAgent = customerSupportAgent;
  }

  @Override
  public void afterConnectionEstablished(WebSocketSession session) throws Exception {
    session.sendMessage(new TextMessage(WELCOME));
  }

  @Override
  protected void handleTextMessage(WebSocketSession session, TextMessage message) {
    String userMessage = message.getPayload();
    log.debug("WebSocket [{}] user -> {}", session.getId(), userMessage);
    try {
      customerSupportAgent.chatStream(session.getId(), userMessage)
          .subscribe(
              chunk -> sendChunk(session, chunk),
              error -> {
                log.error("Streaming error for session {}", session.getId(), error);
                sendChunk(session, GENERIC_FAILURE);
              });
    } catch (PromptInjectionBlockedException e) {
      log.error("Guardrail blocked request: {}", e.getMessage());
      sendChunk(session, GUARDRAIL_FAILURE);
    }
  }

  private void sendChunk(WebSocketSession session, String chunk) {
    synchronized (session) {
      if (!session.isOpen()) {
        return;
      }
      try {
        session.sendMessage(new TextMessage(chunk));
      } catch (IOException e) {
        throw new UncheckedIOException(e);
      }
    }
  }

  @Override
  public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
    // Memory per session until the app is restarted.
  }
}
