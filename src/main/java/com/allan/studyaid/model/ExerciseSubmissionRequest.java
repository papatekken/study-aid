package com.allan.studyaid.model;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record ExerciseSubmissionRequest(

        @NotBlank
        String studentId,

        @NotEmpty
        @Valid
        List<AnswerSubmission> answers
) {
    public record AnswerSubmission(
            @NotBlank String exerciseId,
            @NotBlank String studentAnswer
    ) {}
}
