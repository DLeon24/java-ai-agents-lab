package com.dleon.multimodal.service.impl;

import com.dleon.multimodal.exception.ImageGenerationException;
import org.junit.jupiter.api.Test;
import org.springframework.ai.audio.tts.TextToSpeechPrompt;
import org.springframework.ai.openai.OpenAiAudioSpeechModel;
import org.springframework.ai.openai.OpenAiAudioTranscriptionModel;
import org.springframework.core.io.Resource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class OpenAiVoiceServiceImplTest {

  @Test
  void textToSpeechWrapsProviderErrors() {
    OpenAiAudioTranscriptionModel transcriptionModel = mock(OpenAiAudioTranscriptionModel.class);
    OpenAiAudioSpeechModel speechModel = mock(OpenAiAudioSpeechModel.class);
    when(speechModel.call(any(TextToSpeechPrompt.class))).thenThrow(new RuntimeException("boom"));

    OpenAiVoiceServiceImpl service = new OpenAiVoiceServiceImpl(transcriptionModel, speechModel);

    assertThrows(ImageGenerationException.class, () -> service.textToSpeech("hello", "alloy"));
  }

  @Test
  void transcribeWrapsProviderErrors() {
    OpenAiAudioTranscriptionModel transcriptionModel = mock(OpenAiAudioTranscriptionModel.class);
    OpenAiAudioSpeechModel speechModel = mock(OpenAiAudioSpeechModel.class);
    when(transcriptionModel.call(any(Resource.class))).thenThrow(new RuntimeException("boom"));

    OpenAiVoiceServiceImpl service = new OpenAiVoiceServiceImpl(transcriptionModel, speechModel);

    assertThrows(ImageGenerationException.class,
        () -> service.transcribe(mock(org.springframework.core.io.Resource.class)));
  }

  @Test
  void textToSpeechReturnsAudioResource() throws Exception {
    OpenAiAudioTranscriptionModel transcriptionModel = mock(OpenAiAudioTranscriptionModel.class);
    OpenAiAudioSpeechModel speechModel = mock(OpenAiAudioSpeechModel.class);
    var response =
        mock(org.springframework.ai.audio.tts.TextToSpeechResponse.class, RETURNS_DEEP_STUBS);
    when(response.getResult().getOutput()).thenReturn("abc".getBytes());
    when(speechModel.call(any(TextToSpeechPrompt.class))).thenReturn(response);

    OpenAiVoiceServiceImpl service = new OpenAiVoiceServiceImpl(transcriptionModel, speechModel);

    byte[] out = service.textToSpeech("hello", "alloy").getContentAsByteArray();
    assertEquals(3, out.length);
  }
}
