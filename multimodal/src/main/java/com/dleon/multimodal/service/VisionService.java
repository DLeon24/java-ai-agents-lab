package com.dleon.multimodal.service;

import org.springframework.web.multipart.MultipartFile;

public interface VisionService {

  /**
   * Analyzes an image and answers an optional question about it.
   */
  String describe(MultipartFile image, String question);
}
