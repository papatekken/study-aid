package com.allan.studyaid.model;

import com.fasterxml.jackson.annotation.JsonClassDescription;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;

import java.util.List;

@JsonClassDescription("Grading results for a batch of submitted exercise answers")
public class GradingResult {

    @JsonPropertyDescription("One grade per submitted exercise, in the same order they were given")
    public List<QuestionGrade> grades;

    @JsonPropertyDescription("A short overall improvement suggestion covering the whole batch, " +
            "naming the weakest topic(s) if any")
    public String overallSuggestion;

    @JsonClassDescription("The grade for a single submitted answer")
    public static class QuestionGrade {

        @JsonPropertyDescription("Must be copied EXACTLY from the exerciseId given in the prompt for this question")
        public String exerciseId;

        @JsonPropertyDescription("Whether the student's answer is correct. Accept reasonable equivalent " +
                "phrasing/formatting for short answers, not just exact string matches.")
        public boolean correct;

        @JsonPropertyDescription("Brief, encouraging feedback explaining why it was right/wrong and, if wrong, " +
                "what the correct approach is")
        public String feedback;
    }
}
