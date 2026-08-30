package com.allan.studyaid.service;

import com.allan.studyaid.model.ExerciseGenerationRequest;
import com.allan.studyaid.model.ExerciseGenerationResponse;
import com.allan.studyaid.model.ExerciseView;
import com.allan.studyaid.model.GeneratedExercises;
import com.allan.studyaid.store.ExerciseStore;
import com.allan.studyaid.store.StoredExercise;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class ExerciseGenerationService {

    private final ClaudeService claudeService;
    private final ExerciseStore exerciseStore;

    public ExerciseGenerationService(ClaudeService claudeService, ExerciseStore exerciseStore) {
        this.claudeService = claudeService;
        this.exerciseStore = exerciseStore;
    }

    public ExerciseGenerationResponse generate(ExerciseGenerationRequest request) {
        String systemPrompt = buildSystemPrompt(request);
        String userMessage = "Generate %d practice exercises now.".formatted(request.count());

        GeneratedExercises generated = claudeService.askStructured(
                systemPrompt, List.of(), userMessage, GeneratedExercises.class);

        List<ExerciseView> views = generated.exercises.stream()
                .map(item -> {
                    StoredExercise stored = new StoredExercise(
                            null,
                            request.studentId(),
                            request.subject(),
                            item.question,
                            item.type,
                            item.correctAnswer,
                            item.topic,
                            Instant.now()
                    );
                    String id = exerciseStore.save(stored);
                    return new ExerciseView(id, item.question, item.type, item.options, item.topic);
                })
                .toList();

        return new ExerciseGenerationResponse(views);
    }

    private String buildSystemPrompt(ExerciseGenerationRequest request) {
        String topicClause = request.topic() != null && !request.topic().isBlank()
                ? "Focus specifically on the topic: " + request.topic() + "."
                : "Choose a sensible spread of topics appropriate to the class/age level.";

        String difficultyClause = request.difficulty() != null
                ? "Target difficulty: %d/5.".formatted(request.difficulty())
                : "Choose a difficulty appropriate to the stated class/age.";

        return """
            You are creating practice exercises for a student studying %s.
            Student level: %s.
            %s
            %s

            Mix 'multiple_choice' and 'short_answer' question types unless the topic clearly favours one.
            Each exercise must have a clear, unambiguous correct answer.
            Always respond using the provided tool schema only — do not include any prose outside of it.
            """.formatted(request.subject(), request.classOrAge(), topicClause, difficultyClause);
    }
}
