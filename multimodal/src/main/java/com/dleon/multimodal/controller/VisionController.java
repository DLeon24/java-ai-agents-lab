package com.dleon.multimodal.controller;

import com.dleon.multimodal.dto.VisionResponse;
import com.dleon.multimodal.service.VisionService;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@Validated
@RequestMapping("/api/vision")
@Tag(name = "Multimodal - Vision",
    description = "Image analysis using GPT-4o Vision with Spring AI")
public class VisionController {

  private final VisionService visionService;

  public VisionController(VisionService visionService) {
    this.visionService = visionService;
  }

  @Operation(summary = "Describe/analyze image",
      description = "Uploads an image and asks a question. GPT-4o Vision analyzes and responds.")
  @PostMapping(value = "/describe", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public VisionResponse describe(@RequestPart("image") MultipartFile image,
      @RequestPart(value = "question", required = false) String question) {
    String userQuestion =
        (question != null && !question.isBlank()) ? question : "Describe this image in detail.";
    String answer = visionService.describe(image, question);
    return new VisionResponse(answer, userQuestion);
  }

}
