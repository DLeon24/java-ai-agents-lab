package com.dleon.multimodal.controller;

import com.dleon.multimodal.exception.ImageGenerationException;
import com.dleon.multimodal.service.VisionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(VisionController.class)
class VisionControllerValidationTest {

  @Autowired
  private MockMvc mockMvc;

  @MockitoBean
  private VisionService visionService;

  @Test
  void describeReturnsBadGatewayOnProviderFailure() throws Exception {
    MockMultipartFile image =
        new MockMultipartFile("image", "sample.jpg", MediaType.IMAGE_JPEG_VALUE,
            "image-content".getBytes());
    when(visionService.describe(any(), any())).thenThrow(
        new ImageGenerationException("Error analyzing image"));

    mockMvc.perform(
            multipart("/api/vision/describe").file(image).param("question", "What is this?"))
        .andExpect(status().isBadGateway());
  }
}
