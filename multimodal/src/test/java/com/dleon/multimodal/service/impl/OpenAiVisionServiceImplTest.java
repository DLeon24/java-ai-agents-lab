package com.dleon.multimodal.service.impl;

import com.dleon.multimodal.exception.ImageGenerationException;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.mock.web.MockMultipartFile;

import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class OpenAiVisionServiceImplTest {

  @Test
  void describeRejectsEmptyImage() {
    ChatClient.Builder builder = mock(ChatClient.Builder.class);
    ChatClient chatClient = mock(ChatClient.class, RETURNS_DEEP_STUBS);
    when(builder.build()).thenReturn(chatClient);
    OpenAiVisionServiceImpl service = new OpenAiVisionServiceImpl(builder);
    MockMultipartFile empty = new MockMultipartFile("image", new byte[0]);

    assertThrows(IllegalArgumentException.class, () -> service.describe(empty, "what is this?"));
  }

  @Test
  void describeRejectsInvalidMimeType() {
    ChatClient.Builder builder = mock(ChatClient.Builder.class);
    ChatClient chatClient = mock(ChatClient.class, RETURNS_DEEP_STUBS);
    when(builder.build()).thenReturn(chatClient);
    OpenAiVisionServiceImpl service = new OpenAiVisionServiceImpl(builder);
    MockMultipartFile textFile =
        new MockMultipartFile("image", "note.txt", "text/plain", "hello".getBytes());

    assertThrows(IllegalArgumentException.class, () -> service.describe(textFile, "what is this?"));
  }

  @Test
  void describeWrapsProviderErrors() {
    ChatClient.Builder builder = mock(ChatClient.Builder.class);
    ChatClient chatClient = mock(ChatClient.class, RETURNS_DEEP_STUBS);
    when(builder.build()).thenReturn(chatClient);
    when(chatClient.prompt()
        .user(org.mockito.ArgumentMatchers.<Consumer<ChatClient.PromptUserSpec>>any())).thenThrow(
        new RuntimeException("boom"));
    OpenAiVisionServiceImpl service = new OpenAiVisionServiceImpl(builder);
    MockMultipartFile image =
        new MockMultipartFile("image", "sample.jpg", "image/jpeg", "img".getBytes());

    assertThrows(ImageGenerationException.class, () -> service.describe(image, "what is this?"));
  }
}
