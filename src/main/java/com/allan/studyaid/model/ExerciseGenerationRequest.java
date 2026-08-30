package com.allan.studyaid.model;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ExerciseGenerationRequest(

        @NotBlank
        String studentId,

        @NotNull
        Subject subject,

        /**
         * Free text describing the student's level, e.g. "Year 5", "age 10", "GCSE Foundation".
         * Passed straight into the prompt so it doesn't need to match any fixed enum.
         */
        @NotBlank
        String classOrAge,

        /**
         * Optional topic focus, e.g. "fractions", "past tense verbs". If omitted, Claude
         * picks a reasonable spread of topics for the class/age and subject.
         */
        String topic,

        @Min(1) @Max(20)
        Integer count,

        /**
         * 1 (easier) - 5 (harder). Optional — Claude infers a sensible default from classOrAge if omitted.
         */
        @Min(1) @Max(5)
        Integer difficulty
) {
    public ExerciseGenerationRequest {
        if (count == null) count = 5;
    }
}
