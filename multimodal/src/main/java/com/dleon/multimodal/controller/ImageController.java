package com.dleon.multimodal.controller;

import com.dleon.multimodal.dto.ImageUrlResponse;
import com.dleon.multimodal.service.ImageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@Validated
@RequestMapping("/api/image")
@Tag(name = "Multimodal - Image", description = "Image generation using Spring AI and OpenAI")
public class ImageController {

  private final ImageService imageService;

  public ImageController(ImageService imageService) {
    this.imageService = imageService;
  }

  @Operation(summary = "Generate image (URL)",
      description = "Generates a 1024x1024 image and returns a temporary OpenAI URL")
  @PostMapping("/generate-url")
  public ImageUrlResponse generateUrl(
      @RequestParam @NotBlank(message = "prompt must not be blank") String prompt) {
    String url = imageService.generateImageUrl(prompt);
    return new ImageUrlResponse(url, prompt);
  }

  @Operation(summary = "Generate image (PNG)",
      description = "Generates a 1024x1024 image and returns the PNG bytes directly")
  @PostMapping(value = "/generate-png", produces = MediaType.IMAGE_PNG_VALUE)
  public byte[] generatePng(
      @RequestParam @NotBlank(message = "prompt must not be blank") String prompt) {
    return imageService.generateImageBytes(prompt);
  }
}
