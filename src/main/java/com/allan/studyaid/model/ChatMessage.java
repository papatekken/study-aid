package com.allan.studyaid.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

/**
 * A single turn in a client-supplied conversation history.
 * Client is responsible for sending prior turns back on each request (stateless server).
 */
public record ChatMessage(
        @Pattern(regexp = "user|assistant", message = "role must be 'user' or 'assistant'")
        String role,

        @NotBlank
        String content
) {}
