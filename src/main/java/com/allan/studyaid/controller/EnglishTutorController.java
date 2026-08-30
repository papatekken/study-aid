package com.allan.studyaid.controller;

import com.allan.studyaid.model.EnglishTutorResponse;
import com.allan.studyaid.model.TutorRequest;
import com.allan.studyaid.service.EnglishTutorService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/tutor/english")
public class EnglishTutorController {

    private final EnglishTutorService englishTutorService;

    public EnglishTutorController(EnglishTutorService englishTutorService) {
        this.englishTutorService = englishTutorService;
    }

    @PostMapping("/ask")
    public EnglishTutorResponse ask(@Valid @RequestBody TutorRequest request) {
        return englishTutorService.review(request);
    }
}
