package com.dleon.image.service;

import com.dleon.image.service.impl.OpenAiImageServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.ai.image.ImageModel;
import org.springframework.ai.image.ImageResponse;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class OpenAiImageServiceTest {

  @Test
  void generateImageUrlThrowsWhenProviderReturnsNoData() {
    ImageModel imageModel = mock(ImageModel.class);
    ImageResponse imageResponse = mock(ImageResponse.class, RETURNS_DEEP_STUBS);
    when(imageResponse.getResult().getOutput().getUrl()).thenReturn(null);
    when(imageResponse.getResult().getOutput().getB64Json()).thenReturn(null);
    when(imageModel.call(any())).thenReturn(imageResponse);

    OpenAiImageServiceImpl service = new OpenAiImageServiceImpl(imageModel);

    assertThrows(IllegalStateException.class, () -> service.generateImageUrl("sunset"));
  }

  @Test
  void generateImageBytesThrowsWhenProviderReturnsNoData() {
    ImageModel imageModel = mock(ImageModel.class);
    ImageResponse imageResponse = mock(ImageResponse.class, RETURNS_DEEP_STUBS);
    when(imageResponse.getResult().getOutput().getUrl()).thenReturn(null);
    when(imageResponse.getResult().getOutput().getB64Json()).thenReturn(null);
    when(imageModel.call(any())).thenReturn(imageResponse);

    OpenAiImageServiceImpl service = new OpenAiImageServiceImpl(imageModel);

    assertThrows(IllegalStateException.class, () -> service.generateImageBytes("sunset"));
  }
}
