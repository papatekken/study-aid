package com.allan.studyaid.store;

import com.allan.studyaid.model.Subject;

import java.time.Instant;

public record StoredExercise(
        String exerciseId,
        String studentId,
        Subject subject,
        String question,
        String type,
        String correctAnswer,
        String topic,
        Instant createdAt
) {}
