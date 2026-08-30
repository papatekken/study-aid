package com.allan.studyaid.service;

import com.allan.studyaid.model.ExerciseSubmissionRequest;
import com.allan.studyaid.model.ExerciseSubmissionRequest.AnswerSubmission;
import com.allan.studyaid.model.ExerciseSubmissionResponse;
import com.allan.studyaid.model.ExerciseSubmissionResponse.QuestionResult;
import com.allan.studyaid.model.GradingResult;
import com.allan.studyaid.model.Subject;
import com.allan.studyaid.store.ExerciseStore;
import com.allan.studyaid.store.PerformanceRecord;
import com.allan.studyaid.store.PerformanceStore;
import com.allan.studyaid.store.StoredExercise;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
public class ExerciseGradingService {

    private final ClaudeService claudeService;
    private final ExerciseStore exerciseStore;
    private final PerformanceStore performanceStore;

    public ExerciseGradingService(ClaudeService claudeService, ExerciseStore exerciseStore,
                                   PerformanceStore performanceStore) {
        this.claudeService = claudeService;
        this.exerciseStore = exerciseStore;
        this.performanceStore = performanceStore;
    }

    public ExerciseSubmissionResponse grade(ExerciseSubmissionRequest request) {
        // Resolve each submitted answer against the exercise we generated and stored earlier.
        List<ResolvedAnswer> resolved = request.answers().stream()
                .map(a -> new ResolvedAnswer(a, exerciseStore.find(a.exerciseId())
                        .orElseThrow(() -> new NoSuchElementException(
                                "Unknown or expired exerciseId: " + a.exerciseId()))))
                .toList();

        GradingResult gradingResult = claudeService.askStructured(
                buildSystemPrompt(), List.of(), buildGradingPrompt(resolved), GradingResult.class);

        Map<String, GradingResult.QuestionGrade> gradesById = gradingResult.grades.stream()
                .collect(Collectors.toMap(g -> g.exerciseId, g -> g, (a, b) -> a, LinkedHashMap::new));

        List<QuestionResult> results = new ArrayList<>();
        int correctCount = 0;
        Map<Subject, List<String>> weakByTopic = new LinkedHashMap<>();
        Map<Subject, List<String>> strongByTopic = new LinkedHashMap<>();
        Map<Subject, Integer> correctBySubject = new LinkedHashMap<>();
        Map<Subject, Integer> totalBySubject = new LinkedHashMap<>();

        for (ResolvedAnswer ra : resolved) {
            StoredExercise ex = ra.exercise();
            GradingResult.QuestionGrade grade = gradesById.get(ex.exerciseId());
            boolean correct = grade != null && grade.correct;
            String feedback = grade != null ? grade.feedback : "Could not be graded automatically — please retry.";

            totalBySubject.merge(ex.subject(), 1, Integer::sum);
            if (correct) {
                correctCount++;
                correctBySubject.merge(ex.subject(), 1, Integer::sum);
                strongByTopic.computeIfAbsent(ex.subject(), k -> new ArrayList<>()).add(ex.topic());
            } else {
                weakByTopic.computeIfAbsent(ex.subject(), k -> new ArrayList<>()).add(ex.topic());
            }

            results.add(new QuestionResult(ex.exerciseId(), ex.question(), ra.answer().studentAnswer(),
                    ex.correctAnswer(), correct, feedback));
        }

        // One record per subject present in this batch, so mixed-subject submissions still attribute correctly.
        for (Subject subject : totalBySubject.keySet()) {
            performanceStore.addRecord(request.studentId(), new PerformanceRecord(
                    subject,
                    Instant.now(),
                    correctBySubject.getOrDefault(subject, 0),
                    totalBySubject.get(subject),
                    weakByTopic.getOrDefault(subject, List.of()),
                    strongByTopic.getOrDefault(subject, List.of())
            ));
        }

        return new ExerciseSubmissionResponse(correctCount, resolved.size(), results, gradingResult.overallSuggestion);
    }

    private String buildSystemPrompt() {
        return """
            You are grading a student's practice exercise answers.
            For each question, you are given its correct answer and the student's submitted answer.
            Accept reasonable equivalent phrasing, spelling variation, or formatting for short answers —
            grade the underlying understanding, not exact string matching.
            Always respond using the provided tool schema only — do not include any prose outside of it.
            """;
    }

    private String buildGradingPrompt(List<ResolvedAnswer> resolved) {
        StringBuilder sb = new StringBuilder("Grade the following submitted answers:\n\n");
        for (ResolvedAnswer ra : resolved) {
            sb.append("exerciseId: ").append(ra.exercise().exerciseId()).append("\n")
              .append("question: ").append(ra.exercise().question()).append("\n")
              .append("correctAnswer: ").append(ra.exercise().correctAnswer()).append("\n")
              .append("studentAnswer: ").append(ra.answer().studentAnswer()).append("\n\n");
        }
        return sb.toString();
    }

    private record ResolvedAnswer(AnswerSubmission answer, StoredExercise exercise) {}
}
