package com.dleon.multimodal.service.impl;

import com.dleon.multimodal.exception.ImageGenerationException;
import com.dleon.multimodal.service.VisionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.multipart.MultipartFile;

@Service
public class OpenAiVisionServiceImpl implements VisionService {

  private static final Logger LOG = LoggerFactory.getLogger(OpenAiVisionServiceImpl.class);
  private static final String DEFAULT_QUESTION = "Describe this image in detail.";

  private final ChatClient chatClient;

  public OpenAiVisionServiceImpl(ChatClient.Builder chatClientBuilder) {
    this.chatClient = chatClientBuilder.build();
  }

  @Override
  public String describe(MultipartFile image, String question) {
    if (image == null || image.isEmpty()) {
      throw new IllegalArgumentException("image is required and must not be empty");
    }
    if (image.getContentType() == null || !image.getContentType().startsWith("image/")) {
      throw new IllegalArgumentException("image must be a valid image content type");
    }

    String userQuestion = (question != null && !question.isBlank()) ? question : DEFAULT_QUESTION;

    try {
      var mimeType = MimeTypeUtils.parseMimeType(image.getContentType());
      LOG.info("Analyzing image with question: {}", userQuestion);

      return chatClient.prompt()
          .user(userSpec -> userSpec.text(userQuestion).media(mimeType, image.getResource())).call()
          .content();
    } catch (IllegalArgumentException ex) {
      throw ex;
    } catch (Exception ex) {
      throw new ImageGenerationException("Error analyzing image", ex);
    }
  }
}
