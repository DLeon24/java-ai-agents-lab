package com.dleon.multimodal.service.impl;

import com.dleon.multimodal.exception.ImageGenerationException;
import com.dleon.multimodal.service.VoiceService;
import org.springframework.ai.audio.tts.TextToSpeechPrompt;
import org.springframework.ai.openai.OpenAiAudioSpeechModel;
import org.springframework.ai.openai.OpenAiAudioTranscriptionModel;
import org.springframework.ai.openai.api.OpenAiAudioApi;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

@Service
public class OpenAiVoiceServiceImpl implements VoiceService {

  private final OpenAiAudioTranscriptionModel transcriptionModel;
  private final OpenAiAudioSpeechModel speechModel;

  public OpenAiVoiceServiceImpl(OpenAiAudioTranscriptionModel transcriptionModel,
      OpenAiAudioSpeechModel speechModel) {
    this.transcriptionModel = transcriptionModel;
    this.speechModel = speechModel;
  }

  @Override
  public Resource textToSpeech(String text, String voice) {
    try {
      var voiceEnum = parseVoice(voice);
      var options =
          org.springframework.ai.openai.OpenAiAudioSpeechOptions.builder().voice(voiceEnum)
              .responseFormat(OpenAiAudioApi.SpeechRequest.AudioResponseFormat.MP3).speed(1.0)
              .build();
      var speechBytes =
          speechModel.call(new TextToSpeechPrompt(text, options)).getResult().getOutput();
      return new ByteArrayResource(speechBytes);
    } catch (Exception ex) {
      throw new ImageGenerationException("Error generating speech audio", ex);
    }
  }

  @Override
  public String transcribe(Resource audioResource) {
    try {
      return transcriptionModel.call(audioResource);
    } catch (Exception ex) {
      throw new ImageGenerationException("Error transcribing audio", ex);
    }
  }

  private OpenAiAudioApi.SpeechRequest.Voice parseVoice(String voice) {
    try {
      return OpenAiAudioApi.SpeechRequest.Voice.valueOf(voice.toUpperCase());
    } catch (Exception e) {
      return OpenAiAudioApi.SpeechRequest.Voice.ALLOY;
    }
  }
}
