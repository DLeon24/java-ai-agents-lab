package com.dleon.carrental.agent.resilience;

import io.github.resilience4j.retry.Retry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

@Component
public class ResilientLlmInvoker {

  private static final Logger log = LoggerFactory.getLogger(ResilientLlmInvoker.class);

  private static final String FALLBACK_MESSAGE = """
      Failed to get a response from the AI Model. Are you sure it's up and running, and configured correctly?""";

  private final Retry retry;
  private final Duration timeout;

  public ResilientLlmInvoker(Retry retry,
      @Value("${resilience4j.timelimiter.instances.llmChat.timeout-duration}") Duration timeout) {
    this.retry = retry;
    this.timeout = timeout;
  }

  public String invoke(Supplier<String> llmCall) {
    Supplier<String> retrying = Retry.decorateSupplier(retry, llmCall);
    try {
      return CompletableFuture.supplyAsync(retrying::get)
          .orTimeout(timeout.toMillis(), TimeUnit.MILLISECONDS).get();
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      log.error("LLM call interrupted", e);
      return FALLBACK_MESSAGE;
    } catch (ExecutionException e) {
      log.error("LLM call failed after retries/timeout", e);
      return FALLBACK_MESSAGE;
    }
  }
}
