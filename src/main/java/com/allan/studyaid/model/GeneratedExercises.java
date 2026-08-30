package com.allan.studyaid.model;

import com.fasterxml.jackson.annotation.JsonClassDescription;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;

import java.util.List;

@JsonClassDescription("A set of generated practice exercises for a student")
public class GeneratedExercises {

    @JsonPropertyDescription("The generated exercises, in a sensible teaching order")
    public List<ExerciseItem> exercises;

    @JsonClassDescription("A single practice exercise, including its correct answer for later grading")
    public static class ExerciseItem {

        @JsonPropertyDescription("The exercise question or prompt shown to the student")
        public String question;

        @JsonPropertyDescription("Either 'multiple_choice' or 'short_answer'")
        public String type;

        @JsonPropertyDescription("Answer options if type is 'multiple_choice'. Null/empty for 'short_answer'.")
        public List<String> options;

        @JsonPropertyDescription("The correct answer. For multiple_choice, matches one of the options exactly.")
        public String correctAnswer;

        @JsonPropertyDescription("The specific topic/skill this exercise targets, e.g. 'fractions', 'past tense'")
        public String topic;
    }
}
