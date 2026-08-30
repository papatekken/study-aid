package com.allan.studyaid.model;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

import java.util.List;

public record TutorRequest(

        @NotBlank
        String studentId,

        @NotBlank
        String question,

        /**
         * 1 = gentle nudge only, 2 = partial explanation, 3 = full worked answer.
         */
        @Min(1) @Max(3)
        Integer hintLevel,

        /**
         * Prior turns of this conversation, oldest first. Optional — omit or send
         * empty for a fresh question. Client owns persistence since the server is stateless.
         */
        @Valid
        List<ChatMessage> history
) {
    public TutorRequest {
        if (hintLevel == null) hintLevel = 2;
        if (history == null) history = List.of();
    }
}
