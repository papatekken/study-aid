package com.allan.studyaid.service;

import com.allan.studyaid.model.EnglishTutorResponse;
import com.allan.studyaid.model.TutorRequest;
import org.springframework.stereotype.Service;

@Service
public class EnglishTutorService {

    private final ClaudeService claudeService;

    public EnglishTutorService(ClaudeService claudeService) {
        this.claudeService = claudeService;
    }

    public EnglishTutorResponse review(TutorRequest request) {
        String systemPrompt = buildSystemPrompt(request.hintLevel());
        return claudeService.askStructured(systemPrompt, request.history(), request.question(), EnglishTutorResponse.class);
    }

    private String buildSystemPrompt(int hintLevel) {
        String revealPolicy = switch (hintLevel) {
            case 1 -> "Identify that errors exist and give one general hint about the type of mistake (e.g. " +
                      "'check your verb tenses'), but do NOT list the specific corrections yet. Leave 'correctedText' " +
                      "and 'errors' empty.";
            case 2 -> "List the errors found with their corrections, but keep 'explanation' brief — a sentence per rule.";
            default -> "Provide the fully corrected text, a complete list of errors with corrections, and a thorough " +
                       "explanation of each grammar rule involved.";
        };

        return """
            You are a patient, encouraging English tutor for a student learning grammar, vocabulary,
            and writing. The student may submit a sentence/paragraph to correct, or ask a general
            English question (e.g. vocabulary, reading comprehension).

            %s

            If the input isn't a piece of writing to correct (e.g. a vocabulary question), leave
            'correctedText' and 'errors' empty and answer via 'explanation' instead.

            Always respond using the provided tool schema only — do not include any prose outside of it.
            """.formatted(revealPolicy);
    }
}
