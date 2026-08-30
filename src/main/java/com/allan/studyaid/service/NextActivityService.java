package com.allan.studyaid.service;

import com.allan.studyaid.model.ActivitySuggestion;
import com.allan.studyaid.store.PerformanceRecord;
import com.allan.studyaid.store.PerformanceStore;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NextActivityService {

    private final ClaudeService claudeService;
    private final PerformanceStore performanceStore;

    public NextActivityService(ClaudeService claudeService, PerformanceStore performanceStore) {
        this.claudeService = claudeService;
        this.performanceStore = performanceStore;
    }

    public ActivitySuggestion suggestNext(String studentId) {
        List<PerformanceRecord> records = performanceStore.getRecords(studentId);

        String systemPrompt = """
            You are an educational advisor. Based on a student's practice record across subjects,
            recommend a single next activity to focus on — pick whichever subject/topic needs it most.
            If there is no record yet, recommend starting with a short baseline set of exercises
            in either subject and say so in the rationale.
            Always respond using the provided tool schema only — do not include any prose outside of it.
            """;

        String userMessage = records.isEmpty()
                ? "This student has no recorded practice history yet."
                : "Here is the student's recent practice record:\n\n" + PerformanceSummarizer.summarize(records);

        return claudeService.askStructured(systemPrompt, List.of(), userMessage, ActivitySuggestion.class);
    }
}
