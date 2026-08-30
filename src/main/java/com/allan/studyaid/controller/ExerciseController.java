package com.allan.studyaid.controller;

import com.allan.studyaid.model.ExerciseGenerationRequest;
import com.allan.studyaid.model.ExerciseGenerationResponse;
import com.allan.studyaid.model.ExerciseSubmissionRequest;
import com.allan.studyaid.model.ExerciseSubmissionResponse;
import com.allan.studyaid.service.ExerciseGenerationService;
import com.allan.studyaid.service.ExerciseGradingService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/tutor/exercises")
public class ExerciseController {

    private final ExerciseGenerationService generationService;
    private final ExerciseGradingService gradingService;

    public ExerciseController(ExerciseGenerationService generationService, ExerciseGradingService gradingService) {
        this.generationService = generationService;
        this.gradingService = gradingService;
    }

    @PostMapping("/generate")
    public ExerciseGenerationResponse generate(@Valid @RequestBody ExerciseGenerationRequest request) {
        return generationService.generate(request);
    }

    @PostMapping("/submit")
    public ExerciseSubmissionResponse submit(@Valid @RequestBody ExerciseSubmissionRequest request) {
        return gradingService.grade(request);
    }
}
