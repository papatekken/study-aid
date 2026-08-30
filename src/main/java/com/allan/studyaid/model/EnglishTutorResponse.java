package com.allan.studyaid.model;

import com.fasterxml.jackson.annotation.JsonClassDescription;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;

import java.util.List;

@JsonClassDescription("The structured tutoring response for an English question or writing sample")
public class EnglishTutorResponse {

    @JsonPropertyDescription("The student's text with grammar/spelling corrected. Null if the question wasn't " +
            "a piece of writing to correct (e.g. a vocabulary question).")
    public String correctedText;

    @JsonPropertyDescription("List of individual errors found, each with the original snippet, the correction, " +
            "and the grammar rule involved")
    public List<GrammarError> errors;

    @JsonPropertyDescription("A student-friendly explanation of the concept or corrections, appropriate to their level")
    public String explanation;

    @JsonPropertyDescription("One short, warm sentence of encouragement for the student")
    public String encouragement;

    @JsonClassDescription("A single grammar or spelling error found in the student's text")
    public static class GrammarError {
        @JsonPropertyDescription("The original incorrect text snippet")
        public String original;

        @JsonPropertyDescription("The corrected version of that snippet")
        public String correction;

        @JsonPropertyDescription("The grammar rule or reason, explained simply")
        public String rule;
    }
}
