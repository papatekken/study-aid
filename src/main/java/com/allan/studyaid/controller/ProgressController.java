package com.allan.studyaid.controller;

import com.allan.studyaid.model.ActivitySuggestion;
import com.allan.studyaid.model.SubjectSuggestionRequest;
import com.allan.studyaid.service.NextActivityService;
import com.allan.studyaid.service.SubjectSuggestionService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/tutor/progress")
@Validated
public class ProgressController {

    private final NextActivityService nextActivityService;
    private final SubjectSuggestionService subjectSuggestionService;

    public ProgressController(NextActivityService nextActivityService,
                               SubjectSuggestionService subjectSuggestionService) {
        this.nextActivityService = nextActivityService;
        this.subjectSuggestionService = subjectSuggestionService;
    }

    /** Suggests the single next activity to focus on, based on the student's record across all subjects. */
    @GetMapping("/{studentId}/next-activity")
    public ActivitySuggestion nextActivity(@PathVariable @NotBlank String studentId) {
        return nextActivityService.suggestNext(studentId);
    }

    /** Suggests an improvement activity for one specific subject. */
    @PostMapping("/suggest")
    public ActivitySuggestion suggestForSubject(@Valid @RequestBody SubjectSuggestionRequest request) {
        return subjectSuggestionService.suggest(request);
    }
}
