package com.dleon.image.service;

public interface ImageService {

  /**
   * Generates an image from the prompt and returns the public URL.
   */
  String generateImageUrl(String prompt);

  /**
   * Generates an image from the prompt and returns PNG bytes.
   */
  byte[] generateImageBytes(String prompt);
}
