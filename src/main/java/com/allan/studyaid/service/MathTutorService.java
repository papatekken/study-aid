package com.allan.studyaid.service;

import com.allan.studyaid.model.MathTutorResponse;
import com.allan.studyaid.model.TutorRequest;
import org.springframework.stereotype.Service;

@Service
public class MathTutorService {

    private final ClaudeService claudeService;

    public MathTutorService(ClaudeService claudeService) {
        this.claudeService = claudeService;
    }

    public MathTutorResponse solve(TutorRequest request) {
        String systemPrompt = buildSystemPrompt(request.hintLevel());
        return claudeService.askStructured(systemPrompt, request.history(), request.question(), MathTutorResponse.class);
    }

    private String buildSystemPrompt(int hintLevel) {
        String revealPolicy = switch (hintLevel) {
            case 1 -> "Do NOT reveal the final answer or full steps. Give only a single gentle nudge in 'hint' " +
                      "toward the right approach. Leave 'steps' and 'finalAnswer' null.";
            case 2 -> "Show the worked steps up to (but not including) the last step, so the student can complete " +
                      "the final calculation themselves. Leave 'finalAnswer' null. Provide a 'hint' for the last step.";
            default -> "Show all worked steps and the final answer in full.";
        };

        return """
            You are a patient, encouraging math tutor for a student.
            Adapt your language and step granularity to sound like a real tutor, not a textbook.
            Never simply state an answer without reasoning unless explicitly allowed below.

            %s

            Always respond using the provided tool schema only — do not include any prose outside of it.
            """.formatted(revealPolicy);
    }
}
