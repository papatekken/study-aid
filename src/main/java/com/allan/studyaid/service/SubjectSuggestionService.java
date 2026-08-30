package com.allan.studyaid.service;

import com.allan.studyaid.model.ActivitySuggestion;
import com.allan.studyaid.model.SubjectSuggestionRequest;
import com.allan.studyaid.store.PerformanceRecord;
import com.allan.studyaid.store.PerformanceStore;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SubjectSuggestionService {

    private final ClaudeService claudeService;
    private final PerformanceStore performanceStore;

    public SubjectSuggestionService(ClaudeService claudeService, PerformanceStore performanceStore) {
        this.claudeService = claudeService;
        this.performanceStore = performanceStore;
    }

    public ActivitySuggestion suggest(SubjectSuggestionRequest request) {
        List<PerformanceRecord> subjectRecords = performanceStore.getRecords(request.studentId()).stream()
                .filter(r -> r.subject() == request.subject())
                .toList();

        String systemPrompt = """
            You are an educational advisor for the subject: %s.
            Recommend one focused improvement activity for this specific subject.
            If there is no record for this subject yet, recommend a sensible starting topic for the
            student's level and say so in the rationale.
            Always respond using the provided tool schema only — do not include any prose outside of it.
            """.formatted(request.subject());

        StringBuilder userMessage = new StringBuilder();
        if (request.classOrAge() != null && !request.classOrAge().isBlank()) {
            userMessage.append("Student level: ").append(request.classOrAge()).append("\n\n");
        }
        userMessage.append(subjectRecords.isEmpty()
                ? "This student has no recorded practice history for this subject yet."
                : "Here is the student's recent practice record for this subject:\n\n"
                        + PerformanceSummarizer.summarize(subjectRecords));

        return claudeService.askStructured(systemPrompt, List.of(), userMessage.toString(), ActivitySuggestion.class);
    }
}
