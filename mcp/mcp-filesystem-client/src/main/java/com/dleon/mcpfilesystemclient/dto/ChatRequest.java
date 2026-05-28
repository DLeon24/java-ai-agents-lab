package com.dleon.mcpfilesystemclient.dto;

import jakarta.validation.constraints.NotBlank;

public record ChatRequest(@NotBlank(message = "message must not be blank") String message) {
}
