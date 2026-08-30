package com.allan.studyaid.store;

import com.allan.studyaid.model.Subject;

import java.time.Instant;
import java.util.List;

public record PerformanceRecord(
        Subject subject,
        Instant submittedAt,
        int score,
        int total,
        List<String> weakTopics,
        List<String> strongTopics
) {}
