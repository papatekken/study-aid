package com.allan.studyaid.model;

import com.fasterxml.jackson.annotation.JsonClassDescription;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;

import java.util.List;

/**
 * Doubles as the Claude tool-input schema (auto-derived by the SDK from this class's
 * fields) AND the response DTO returned to the client — the tool_use input IS the answer.
 */
@JsonClassDescription("The structured tutoring response for a math question")
public class MathTutorResponse {

    @JsonPropertyDescription("Ordered list of worked reasoning steps, appropriate to the student's level. " +
            "Stop short of the final answer unless hintLevel allows revealing it.")
    public List<String> steps;

    @JsonPropertyDescription("A short Socratic-style nudge pointing the student toward the next step, without giving it away")
    public String hint;

    @JsonPropertyDescription("The final numeric/symbolic answer. Populate only when hintLevel permits revealing it; " +
            "otherwise leave null.")
    public String finalAnswer;

    @JsonPropertyDescription("One short, warm sentence of encouragement for the student")
    public String encouragement;
}
