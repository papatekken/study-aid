package com.allan.studyaid.model;

import java.util.List;

public record ExerciseGenerationResponse(
        List<ExerciseView> exercises
) {}
