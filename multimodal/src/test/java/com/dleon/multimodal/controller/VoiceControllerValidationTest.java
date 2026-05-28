package com.dleon.multimodal.controller;

import com.dleon.multimodal.exception.ImageGenerationException;
import com.dleon.multimodal.service.VoiceService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(VoiceController.class)
class VoiceControllerValidationTest {

  @Autowired
  private MockMvc mockMvc;

  @MockitoBean
  private VoiceService voiceService;

  @Test
  void textToSpeechRejectsBlankText() throws Exception {
    mockMvc.perform(post("/api/voice/tts").param("text", "   ").param("voice", "alloy"))
        .andExpect(status().isBadRequest());
  }

  @Test
  void transcribeReturnsBadGatewayOnProviderFailure() throws Exception {
    MockMultipartFile audio =
        new MockMultipartFile("audio", "sample.mp3", MediaType.APPLICATION_OCTET_STREAM_VALUE,
            "audio-content".getBytes());
    when(voiceService.transcribe(audio.getResource())).thenThrow(
        new ImageGenerationException("Error transcribing audio"));

    mockMvc.perform(multipart("/api/voice/transcribe").file(audio))
        .andExpect(status().isBadGateway());
  }

  @Test
  void transcribeRejectsEmptyAudio() throws Exception {
    MockMultipartFile emptyAudio =
        new MockMultipartFile("audio", "empty.mp3", MediaType.APPLICATION_OCTET_STREAM_VALUE,
            new byte[0]);

    mockMvc.perform(multipart("/api/voice/transcribe").file(emptyAudio))
        .andExpect(status().isBadRequest());
  }
}
