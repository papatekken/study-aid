package com.allan.studyaid.model;

import java.util.List;

public record ExerciseSubmissionResponse(
        int score,
        int total,
        List<QuestionResult> results,
        String improvementSuggestion
) {
    public record QuestionResult(
            String exerciseId,
            String question,
            String studentAnswer,
            String correctAnswer,
            boolean correct,
            String feedback
    ) {}
}
