package com.dleon.image.controller;

import com.dleon.image.service.ImageService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ImageController.class)
class ImageControllerValidationTest {

  @Autowired
  private MockMvc mockMvc;

  @MockitoBean
  private ImageService imageService;

  @Test
  void generateUrlRejectsBlankPrompt() throws Exception {
    mockMvc.perform(post("/api/image/generate-url").param("prompt", "   "))
        .andExpect(status().isBadRequest());
  }

  @Test
  void generatePngRejectsBlankPrompt() throws Exception {
    mockMvc.perform(post("/api/image/generate-png").param("prompt", "   "))
        .andExpect(status().isBadRequest());
  }
}
