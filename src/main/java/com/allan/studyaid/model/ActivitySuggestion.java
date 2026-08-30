package com.allan.studyaid.model;

import com.fasterxml.jackson.annotation.JsonClassDescription;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;

import java.util.List;

@JsonClassDescription("A recommended next practice activity for a student")
public class ActivitySuggestion {

    @JsonPropertyDescription("Which subject this recommendation focuses on")
    public String subject;

    @JsonPropertyDescription("The specific topic/skill to focus on next, based on the student's record")
    public String focusTopic;

    @JsonPropertyDescription("A short, student-friendly explanation of why this focus was chosen")
    public String rationale;

    @JsonPropertyDescription("2-4 concrete suggested activities or exercise types the student could do next")
    public List<String> suggestedActivities;
}
