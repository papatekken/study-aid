package com.allan.studyaid.store;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * In-memory only, as agreed for this stage of the project — swap for a real store
 * (Postgres/Redis) once this needs to survive a restart or scale beyond one instance.
 */
@Component
public class ExerciseStore {

    private final Map<String, StoredExercise> exercises = new ConcurrentHashMap<>();

    public String save(StoredExercise exerciseWithoutId) {
        String id = UUID.randomUUID().toString();
        StoredExercise withId = new StoredExercise(
                id,
                exerciseWithoutId.studentId(),
                exerciseWithoutId.subject(),
                exerciseWithoutId.question(),
                exerciseWithoutId.type(),
                exerciseWithoutId.correctAnswer(),
                exerciseWithoutId.topic(),
                exerciseWithoutId.createdAt()
        );
        exercises.put(id, withId);
        return id;
    }

    public Optional<StoredExercise> find(String exerciseId) {
        return Optional.ofNullable(exercises.get(exerciseId));
    }
}
