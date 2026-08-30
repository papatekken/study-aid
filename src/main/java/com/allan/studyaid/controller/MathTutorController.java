package com.allan.studyaid.controller;

import com.allan.studyaid.model.MathTutorResponse;
import com.allan.studyaid.model.TutorRequest;
import com.allan.studyaid.service.MathTutorService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/tutor/math")
public class MathTutorController {

    private final MathTutorService mathTutorService;

    public MathTutorController(MathTutorService mathTutorService) {
        this.mathTutorService = mathTutorService;
    }

    @PostMapping("/ask")
    public MathTutorResponse ask(@Valid @RequestBody TutorRequest request) {
        return mathTutorService.solve(request);
    }
}
