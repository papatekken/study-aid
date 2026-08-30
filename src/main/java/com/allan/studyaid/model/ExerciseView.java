package com.allan.studyaid.model;

import java.util.List;

public record ExerciseView(
        String exerciseId,
        String question,
        String type,
        List<String> options,
        String topic
) {}
