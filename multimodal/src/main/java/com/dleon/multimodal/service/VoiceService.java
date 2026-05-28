package com.dleon.multimodal.service;

import org.springframework.core.io.Resource;

public interface VoiceService {

  /**
   * Convierte texto a audio MP3 usando OpenAI TTS.
   */
  Resource textToSpeech(String text, String voice);

  /**
   * Transcribe un archivo de audio a texto usando OpenAI Whisper.
   */
  String transcribe(Resource audioResource);
}
