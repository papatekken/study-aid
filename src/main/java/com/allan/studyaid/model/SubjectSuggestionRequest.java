package com.allan.studyaid.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record SubjectSuggestionRequest(

        @NotBlank
        String studentId,

        @NotNull
        Subject subject,

        /**
         * Optional — helps produce a sensible suggestion even if the student has no record yet.
         */
        String classOrAge
) {}
