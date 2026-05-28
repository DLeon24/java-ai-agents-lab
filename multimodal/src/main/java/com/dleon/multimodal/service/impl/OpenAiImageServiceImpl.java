package com.dleon.multimodal.service.impl;

import com.dleon.multimodal.exception.ImageGenerationException;
import com.dleon.multimodal.service.ImageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.image.ImageModel;
import org.springframework.ai.image.ImageOptionsBuilder;
import org.springframework.ai.image.ImagePrompt;
import org.springframework.ai.image.ImageResponse;
import org.springframework.stereotype.Service;

import java.util.Base64;

@Service
public class OpenAiImageServiceImpl implements ImageService {

  private static final Logger LOG = LoggerFactory.getLogger(OpenAiImageServiceImpl.class);

  private final ImageModel imageModel;

  public OpenAiImageServiceImpl(ImageModel imageModel) {
    this.imageModel = imageModel;
  }

  @Override
  public String generateImageUrl(String prompt) {
    LOG.info("Generating image (URL) for prompt: {}", prompt);
    var output = requireOutputResponse(prompt).getResult().getOutput();
    // gpt-image-1 can return base64; expose as a data URI for the caller.
    if (output.getUrl() != null && !output.getUrl().isBlank()) {
      return output.getUrl();
    }
    if (output.getB64Json() == null || output.getB64Json().isBlank()) {
      throw new IllegalStateException("Image provider returned empty output");
    }
    return "data:image/png;base64," + output.getB64Json();
  }

  @Override
  public byte[] generateImageBytes(String prompt) {
    LOG.info("Generating image (bytes) for prompt: {}", prompt);
    var output = requireOutputResponse(prompt).getResult().getOutput();
    if (output.getB64Json() != null && !output.getB64Json().isBlank()) {
      return Base64.getDecoder().decode(output.getB64Json());
    }
    if (output.getUrl() == null || output.getUrl().isBlank()) {
      throw new IllegalStateException("Image provider returned empty output");
    }
    try {
      return new java.net.URI(output.getUrl()).toURL().openStream().readAllBytes();
    } catch (Exception e) {
      throw new ImageGenerationException("Error downloading generated image", e);
    }
  }

  private ImageResponse generate(String prompt) {
    var options = ImageOptionsBuilder.builder().width(1024).height(1024).build();
    return imageModel.call(new ImagePrompt(prompt, options));
  }

  private ImageResponse requireOutputResponse(String prompt) {
    try {
      var response = generate(prompt);
      if (response == null || response.getResult() == null || response.getResult()
          .getOutput() == null) {
        throw new IllegalStateException("Image provider returned empty output");
      }
      return response;
    } catch (ImageGenerationException | IllegalStateException ex) {
      throw ex;
    } catch (Exception ex) {
      throw new ImageGenerationException("Error generating image", ex);
    }
  }
}
